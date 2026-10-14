"use strict";

// HTML 요소를 찾아두면 이후 화면 내용을 바꾸거나 버튼 클릭을 연결할 수 있습니다.
const ui = {
    routeButton: document.getElementById("route-button"),
    routeResults: document.getElementById("route-results"),
    routeList: document.getElementById("route-list"),
    routeStatus: document.getElementById("route-status"),
    category: document.getElementById("place-category"),
    areaOptions: document.getElementById("area-options"),
    resultsTitle: document.getElementById("results-title"),
    searchInfo: document.getElementById("place-search-info"),
    searchForm: document.getElementById("search-form"),
    searchQuery: document.getElementById("search-query"),
    searchButton: document.getElementById("search-button"),
    searchStatus: document.getElementById("search-status"),
    searchResults: document.getElementById("search-results"),
    selectedLocation: document.getElementById("selected-location"),
    selectedName: document.getElementById("selected-name"),
    selectedAddress: document.getElementById("selected-address"),
    confirm: document.getElementById("confirm-location"),
    participants: document.getElementById("participants"),
    participantCount: document.getElementById("participant-count"),
    participantEmpty: document.getElementById("participant-empty"),
    demo: document.getElementById("demo-button"),
    reset: document.getElementById("reset-button"),
    recommend: document.getElementById("recommend-button"),
    status: document.getElementById("status"),
    results: document.getElementById("results"),
    places: document.getElementById("places"),
    cafeCount: document.getElementById("cafe-count"),
    midpointInfo: document.getElementById("midpoint-info"),
    placeholder: document.getElementById("map-placeholder"),
    placeholderText: document.getElementById("map-placeholder-text")
};

// 검색으로 확정한 출발지만 locations 배열에 저장합니다.
let locations = [];
let map;
let loading = false;
let searching = false;
let selectedLocation = null;
let previewMarker = null;
let participantMarkers = [];
let resultMarkers = [];
let searchCircle;
let selectedAreaId = null;
let routesLoading = false;
let routeDestination = null;
let routeLines = [];

function placeLabel() { return ui.category.value === "RESTAURANT" ? "음식점" : "카페"; }

function showStatus(message, error = false) {
    ui.status.textContent = message;
    ui.status.classList.toggle("error", error);
}

function updateButtons() {
    const ready = Boolean(map) && !loading && !searching && !routesLoading;
    ui.routeButton.disabled = !ready || !routeDestination;
    ui.routeButton.textContent = routesLoading ? "경로 조회 중…" : "시간 · 경로 보기";
    ui.searchQuery.disabled = !ready;
    ui.searchButton.disabled = !ready;
    ui.searchButton.textContent = searching ? "검색 중" : "검색";
    ui.confirm.disabled = !ready || !selectedLocation || locations.length >= 20;
    ui.demo.disabled = !ready;
    ui.category.disabled = !ready;
    // 확정한 참가자가 없어도 검색 내용이나 미리보기는 초기화할 수 있습니다.
    const hasInput = locations.length > 0 || selectedLocation || ui.searchResults.children.length > 0 || ui.searchQuery.value.trim();
    ui.reset.disabled = !ready || !hasInput;
    ui.recommend.disabled = !ready || locations.length < 2;
    ui.recommend.textContent = loading ? "번화가 주변 장소를 찾고 있어요…" : "번화가 & 장소 추천 →";
    document.querySelectorAll(".remove-button, .search-result, .area-option, .transport-select").forEach(control => { control.disabled = !ready; });
}

function clearSelection() {
    if (previewMarker) previewMarker.setMap(null);
    previewMarker = null;
    selectedLocation = null;
    ui.selectedLocation.hidden = true;
    Array.from(ui.searchResults.children).forEach(row => row.children[0].classList.toggle("selected", false));
}

function previewLocation(place, button) {
    if (!map || loading || searching || routesLoading) return;
    clearSelection();
    selectedLocation = place;
    const position = new kakao.maps.LatLng(place.latitude, place.longitude);
    previewMarker = createMarker(position, "선택", "preview");
    map.setLevel(3);
    map.panTo(position);
    button.classList.add("selected");
    ui.selectedName.textContent = place.name;
    ui.selectedAddress.textContent = place.address || "주소 정보 없음";
    ui.selectedLocation.hidden = false;
    ui.searchStatus.textContent = locations.length >= 20
        ? "참가자는 최대 20명입니다. 기존 참가자를 삭제한 뒤 확정해주세요."
        : "지도 위치를 확인한 뒤 출발지로 확정해주세요.";
    updateButtons();
    if (window.matchMedia("(max-width: 760px)").matches) {
        document.getElementById("map").scrollIntoView({ behavior: "auto", block: "start" });
    }
}

function renderSearchResults(places) {
    ui.searchResults.replaceChildren();
    places.forEach(place => {
        const row = document.createElement("li");
        const button = document.createElement("button");
        button.type = "button";
        button.className = "search-result";
        const name = document.createElement("strong");
        name.textContent = place.name;
        const address = document.createElement("span");
        address.textContent = place.address || "주소 정보 없음";
        button.append(name, address);
        button.addEventListener("click", () => previewLocation(place, button));
        row.append(button);
        ui.searchResults.append(row);
    });
    ui.searchStatus.textContent = places.length ? `${places.length}개의 결과를 찾았어요. 원하는 위치를 선택하세요.` : "검색 결과가 없어요. 지역명이나 정확한 주소를 함께 입력해보세요.";
}

async function searchLocations(event) {
    event.preventDefault();
    if (!map || loading || searching || routesLoading) return;
    const query = ui.searchQuery.value.trim();
    if (!query) { ui.searchStatus.textContent = "장소명 또는 주소를 입력해주세요."; return; }
    searching = true;
    clearSelection();
    ui.searchResults.replaceChildren();
    ui.searchStatus.textContent = "출발지를 검색하고 있어요…";
    updateButtons();
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 20000);
    try {
        // encodeURIComponent는 한글·공백·특수문자가 URL 안에서 올바르게 전달되도록 합니다.
        const response = await fetch(`./locations/search?query=${encodeURIComponent(query)}`, { signal: controller.signal });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "출발지 검색에 실패했어요. 잠시 후 다시 시도해주세요.");
        renderSearchResults(data);
    } catch (error) {
        ui.searchStatus.textContent = error.name === "AbortError" ? "검색 응답이 지연되고 있어요. 다시 시도해주세요." : error.message || "서버 연결을 확인해주세요.";
    } finally {
        clearTimeout(timeout);
        searching = false;
        updateButtons();
    }
}

function confirmLocation() {
    if (!map || loading || searching || routesLoading || !selectedLocation) return;
    if (locations.length >= 20) { showStatus("참가자는 최대 20명까지 확정할 수 있어요.", true); return; }
    // 이름·주소는 화면 표시용이고 위도·경도는 계산용입니다.
    locations.push({ ...selectedLocation, transportMode: "TRANSIT" });
    selectedAreaId = null;
    clearSelection();
    ui.searchResults.replaceChildren();
    ui.searchQuery.value = "";
    ui.searchStatus.textContent = "다음 참가자의 출발지를 검색해주세요.";
    clearResults();
    renderParticipants();
    fitParticipants();
    showStatus(locations.length >= 20 ? "참가자 20명을 확정했어요. 추가하려면 기존 참가자를 삭제해주세요." : locations.length < 2 ? "첫 번째 출발지를 확정했어요. 한 곳 이상 더 추가해주세요." : `${locations.length}명의 출발지를 확정했어요. 번화가 & 장소 추천 버튼을 눌러주세요.`);
    ui.searchQuery.focus();
}

function clearResults() {
    clearRoutes();
    routeDestination = null;
    resultMarkers.forEach(marker => marker.setMap(null));
    resultMarkers = [];
    if (searchCircle) searchCircle.setMap(null);
    searchCircle = null;
    ui.places.replaceChildren();
    ui.areaOptions.replaceChildren();
    ui.results.hidden = true;
}

// CustomOverlay를 이용해 참가자·중간지점·카페를 다른 색으로 표시합니다.
function createMarker(position, label, kind, onClick) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = `map-marker ${kind}`;
    button.textContent = label;
    button.setAttribute("aria-label", kind === "participant" ? `참가자 ${label}` : kind === "cafe" ? `카페 ${label}` : kind === "preview" ? "확정 전 위치" : "중간지점");
    if (onClick) button.addEventListener("click", onClick);
    return new kakao.maps.CustomOverlay({ map, position, content: button, yAnchor: 1.2, clickable: true });
}

function renderParticipants() {
    participantMarkers.forEach(marker => marker.setMap(null));
    participantMarkers = [];
    ui.participants.replaceChildren();
    ui.participantCount.textContent = `${locations.length}명`;
    ui.participantEmpty.hidden = locations.length > 0;

    locations.forEach((location, index) => {
        const position = new kakao.maps.LatLng(location.latitude, location.longitude);
        participantMarkers.push(createMarker(position, String(index + 1), "participant", () => map.panTo(position)));

        const row = document.createElement("li");
        row.className = "participant-row";
        const number = document.createElement("span");
        number.className = "participant-number";
        number.textContent = index + 1;
        const detail = document.createElement("div");
        detail.className = "participant-detail";
        detail.textContent = `참가자 ${index + 1}`;
        const name = document.createElement("span");
        name.className = "location-name";
        name.textContent = location.name || "예시 출발지";
        detail.append(name);
        const coordinate = document.createElement("span");
        coordinate.className = "coordinate";
        coordinate.textContent = location.address || `${location.latitude.toFixed(5)}, ${location.longitude.toFixed(5)}`;
        detail.append(coordinate);
        const transportLabel = document.createElement("label");
        transportLabel.className = "transport-label";
        transportLabel.textContent = "이동수단";
        const transport = document.createElement("select");
        transport.className = "transport-select";
        transport.setAttribute("aria-label", `참가자 ${index + 1} 이동수단`);
        for (const [value, label] of [["TRANSIT", "대중교통"], ["CAR", "자차"]]) {
            const option = document.createElement("option");
            option.value = value;
            option.textContent = label;
            transport.append(option);
        }
        transport.value = location.transportMode || "TRANSIT";
        transport.addEventListener("change", () => {
            if (loading || searching || routesLoading) { transport.value = location.transportMode || "TRANSIT"; return; }
            // 변경된 이동수단으로 다음 경로 조회를 수행합니다. 이전 경로는 지웁니다.
            location.transportMode = transport.value === "CAR" ? "CAR" : "TRANSIT";
            transport.value = location.transportMode;
            clearRoutes();
            showStatus(`참가자 ${index + 1}의 이동수단을 ${location.transportMode === "CAR" ? "자차" : "대중교통"}로 선택했어요. 번화가 선택 후 시간 · 경로 보기로 확인해주세요.`);
        });
        transportLabel.append(transport);
        detail.append(transportLabel);
        const remove = document.createElement("button");
        remove.type = "button";
        remove.className = "remove-button";
        remove.textContent = "삭제";
        remove.setAttribute("aria-label", `참가자 ${index + 1} 삭제`);
        remove.addEventListener("click", () => {
            if (loading || searching || routesLoading) return;
            locations.splice(index, 1);
            selectedAreaId = null;
            clearResults();
            renderParticipants();
            fitParticipants();
            if (selectedLocation) ui.searchStatus.textContent = "지도 위치를 확인한 뒤 출발지로 확정해주세요.";
            showStatus("위치를 변경했어요. 다시 검색해주세요.");
        });
        row.append(number, detail, remove);
        ui.participants.append(row);
    });
    updateButtons();
}

function fitParticipants() {
    const bounds = new kakao.maps.LatLngBounds();
    locations.forEach(location => bounds.extend(new kakao.maps.LatLng(location.latitude, location.longitude)));
    if (locations.length) map.setBounds(bounds);
}

function selectPlace(index, position) {
    map.panTo(position);
    Array.from(ui.places.children).forEach((card, cardIndex) => card.classList.toggle("selected", cardIndex === index));
    ui.places.children[index].scrollIntoView({ behavior: "auto", block: "nearest" });
}

function renderRecommendations(data) {
    clearResults();
    selectedAreaId = data.selectedArea.id;
    routeDestination = data.selectedArea;
    const center = data.selectedArea.location;
    const midpoint = new kakao.maps.LatLng(center.latitude, center.longitude);
    const label = data.category === "RESTAURANT" ? "음식점" : "카페";
    ui.resultsTitle.textContent = `추천 ${label}`;
    ui.searchInfo.textContent = `${data.selectedArea.name} 중심 반경 ${data.radiusMeters}m · 중심에서 가까운 순서 (직선거리)`;
    data.areas.forEach((area, index) => {
        const row = document.createElement("li");
        const button = document.createElement("button");
        button.type = "button";
        button.className = "area-option";
        button.classList.toggle("selected", area.id === selectedAreaId);
        button.setAttribute("aria-pressed", String(area.id === selectedAreaId));
        const title = document.createElement("strong");
        title.textContent = `${index + 1}. ${area.name}`;
        const detail = document.createElement("span");
        detail.textContent = `${area.description} · 참가자 최대 직선거리 ${(area.maxDistanceMeters / 1000).toFixed(1)}km`;
        button.append(title, detail);
        button.addEventListener("click", () => { if (!loading && !searching && !routesLoading) recommendCafes(area.id); });
        row.append(button);
        ui.areaOptions.append(row);
    });
    const bounds = new kakao.maps.LatLngBounds();
    bounds.extend(midpoint);
    locations.forEach(location => bounds.extend(new kakao.maps.LatLng(location.latitude, location.longitude)));
    resultMarkers.push(createMarker(midpoint, data.selectedArea.name, "midpoint", () => map.panTo(midpoint)));
    searchCircle = new kakao.maps.Circle({ map, center: midpoint, radius: data.radiusMeters,
        strokeWeight: 2, strokeColor: "#13796e", strokeOpacity: 0.5, strokeStyle: "dash",
        fillColor: "#13796e", fillOpacity: 0.08 });
    ui.midpointInfo.textContent = `선택 지역: ${data.selectedArea.name} · ${data.selectedArea.description}`;
    ui.cafeCount.textContent = `${data.places.length}곳`;

    data.places.forEach((place, index) => {
        const position = new kakao.maps.LatLng(place.latitude, place.longitude);
        bounds.extend(position);
        resultMarkers.push(createMarker(position, String(index + 1), "cafe", () => selectPlace(index, position)));
        const card = document.createElement("li");
        card.className = "place-card";
        const title = document.createElement("div");
        title.className = "place-title";
        const select = document.createElement("button");
        select.type = "button";
        select.className = "place-select";
        select.textContent = `${index + 1}. ${place.name}`;
        select.addEventListener("click", () => selectPlace(index, position));
        const distance = document.createElement("span");
        distance.className = "place-distance";
        distance.textContent = `${place.distanceMeters.toLocaleString()}m`;
        title.append(select, distance);
        const address = document.createElement("p");
        address.className = "place-address";
        address.textContent = place.address || "주소 정보 없음";
        card.append(title, address);
        // 외부 응답을 HTML 문자열로 넣지 않고 textContent로 표시합니다.
        const placeUrl = safePlaceUrl(place.placeUrl);
        if (placeUrl) {
            const link = document.createElement("a");
            link.className = "place-link";
            link.textContent = "카카오맵에서 자세히 보기 ↗";
            link.href = placeUrl.href;
            link.target = "_blank";
            link.rel = "noopener noreferrer";
            card.append(link);
        }
        ui.places.append(card);
    });
    ui.results.hidden = false;
    map.setBounds(bounds);
    showStatus(data.places.length ? `${data.selectedArea.name} 주변 ${label} ${data.places.length}곳을 찾았어요. 다른 번화가 후보도 선택할 수 있어요.` : `${data.selectedArea.name} 반경 ${data.radiusMeters}m 안에 ${label} 검색 결과가 없어요. 다른 번화가 후보를 선택해보세요.`);
}

// 링크 정보에 문제가 있어도 카페 이름·주소·지도 표시는 계속 보여줍니다.
function safePlaceUrl(value) {
    try {
        const url = new URL(value);
        return ["http:", "https:"].includes(url.protocol) && url.hostname === "place.map.kakao.com" && !url.username && !url.password ? url : null;
    } catch {
        return null;
    }
}

async function recommendCafes(areaId = selectedAreaId) {
    if (!map || loading || searching || routesLoading || locations.length < 2) return;
    loading = true;
    clearResults();
    updateButtons();
    showStatus("출발지들의 거리 균형을 비교하고 번화가 주변 장소를 찾고 있어요…");
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 20000);
    try {
        // fetch는 브라우저에서 서버로 요청을 보내는 함수입니다.
        const response = await fetch("./meetings/recommendations/hotspots", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ locations: locations.map(location => ({ latitude: location.latitude, longitude: location.longitude })), category: ui.category.value, areaId }),
            signal: controller.signal
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "장소 검색에 실패했어요. 잠시 후 다시 시도해주세요.");
        renderRecommendations(data);
    } catch (error) {
        clearResults();
        selectedAreaId = null;
        showStatus(error.name === "AbortError" ? "응답 시간이 길어지고 있어요. 잠시 후 다시 시도해주세요." : error.message || "서버 연결을 확인해주세요.", true);
    } finally {
        clearTimeout(timeout);
        loading = false;
        updateButtons();
    }
}


// 경로는 서버가 반환한 실제 좌표로 그립니다. 참가자마다 색을 구분합니다.
function clearRoutes() {
    routeLines.forEach(line => line.setMap(null));
    routeLines = [];
    ui.routeList.replaceChildren();
    ui.routeResults.hidden = true;
    ui.routeStatus.textContent = "";
}

function renderRoutes(routes) {
    const colors = ["#2563eb", "#d97706", "#9333ea", "#db2777", "#059669"];
    const bounds = new kakao.maps.LatLngBounds();
    let success = 0;
    routes.forEach(route => {
        const index = route.participantIndex;
        const card = document.createElement("li");
        card.className = "route-card";
        const title = document.createElement("strong");
        title.textContent = "참가자 " + (index + 1) + " · " + (route.transportMode === "CAR" ? "자차" : "대중교통");
        title.style.color = colors[index % colors.length];
        const detail = document.createElement("p");
        card.append(title, detail);
        if (route.status !== "OK") {
            detail.textContent = route.message || "경로를 찾지 못했어요. 다시 조회해주세요.";
            card.classList.add("route-error");
        } else {
            success++;
            const minutes = Math.ceil(route.durationSeconds / 60);
            const time = minutes >= 60 ? Math.floor(minutes / 60) + "시간 " + (minutes % 60) + "분" : minutes + "분";
            detail.textContent = "약 " + time + " · " + (route.distanceMeters / 1000).toFixed(1) + "km" +
                (route.transportMode === "TRANSIT" ? " · 환승 " + route.transfers + "회" : "");
            const lines = [];
            route.segments.forEach(segment => {
                if (segment.points.length < 2) return;
                const path = segment.points.map(p => new kakao.maps.LatLng(p.latitude, p.longitude));
                path.forEach(p => bounds.extend(p));
                const line = new kakao.maps.Polyline({map, path, strokeWeight: 4,
                    strokeColor: colors[index % colors.length], strokeOpacity: 0.85,
                    strokeStyle: segment.type === "WALKING" ? "dash" : "solid"});
                lines.push(line);
                routeLines.push(line);
            });
            const label = document.createElement("label");
            const toggle = document.createElement("input");
            toggle.type = "checkbox";
            toggle.checked = true;
            toggle.disabled = lines.length === 0;
            toggle.addEventListener("change", () => lines.forEach(line => line.setMap(toggle.checked ? map : null)));
            label.append(toggle, document.createTextNode(" 지도 경로 표시"));
            card.append(label);
            if (route.message) {
                const message = document.createElement("p");
                message.textContent = route.message;
                card.append(message);
            }
        }
        ui.routeList.append(card);
    });
    ui.routeResults.hidden = false;
    ui.routeStatus.textContent = routeDestination.name + "까지 " + success + "/" + routes.length + "명의 경로를 조회했어요.";
    if (routeLines.length) map.setBounds(bounds);
}

async function requestRoutes() {
    if (!map || loading || searching || routesLoading || !routeDestination) return;
    clearRoutes();
    routesLoading = true;
    updateButtons();
    ui.routeStatus.textContent = "참가자별 경로를 조회하고 있어요…";
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), 90000);
    try {
        const response = await fetch("./meetings/routes", {
            method: "POST", headers: {"Content-Type": "application/json"},
            body: JSON.stringify({participants: locations.map(p => ({
                location: {latitude: p.latitude, longitude: p.longitude}, transportMode: p.transportMode || "TRANSIT"
            })), destination: routeDestination.location}), signal: controller.signal
        });
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || "경로 조회에 실패했어요.");
        renderRoutes(data);
    } catch (error) {
        ui.routeStatus.textContent = error.name === "AbortError" ? "경로 조회가 지연되고 있어요. 다시 시도해주세요." : error.message;
    } finally {
        clearTimeout(timeout);
        routesLoading = false;
        updateButtons();
    }
}

ui.routeButton.addEventListener("click", requestRoutes);

function loadKakaoSdk(key) {
    return new Promise((resolve, reject) => {
        const script = document.createElement("script");
        const timeout = setTimeout(() => reject(new Error("지도 로딩이 지연되고 있어요. 인터넷 연결과 카카오 도메인 설정을 확인해주세요.")), 15000);
        script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${encodeURIComponent(key)}&autoload=false`;
        script.onload = () => {
            if (!window.kakao || !kakao.maps) {
                clearTimeout(timeout);
                reject(new Error("카카오 JavaScript 키와 등록 도메인을 확인해주세요."));
                return;
            }
            kakao.maps.load(() => { clearTimeout(timeout); resolve(); });
        };
        script.onerror = () => { clearTimeout(timeout); reject(new Error("지도를 불러오지 못했어요. JavaScript 키와 등록 도메인을 확인해주세요.")); };
        document.head.append(script);
    });
}

async function initialize() {
    try {
        const response = await fetch("./map/config");
        const config = await response.json();
        if (!response.ok || !config.javascriptKey) throw new Error(".env에 KAKAO_JAVASCRIPT_KEY를 설정하고 서버를 다시 실행해주세요.");
        await loadKakaoSdk(config.javascriptKey);
        map = new kakao.maps.Map(document.getElementById("map"), {
            center: new kakao.maps.LatLng(37.505, 127.029), level: 5
        });
        ui.placeholder.hidden = true;
        map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
        // 모바일 화면 크기가 바뀌면 지도 크기도 다시 계산합니다.
        window.addEventListener("resize", () => { const center = map.getCenter(); map.relayout(); map.setCenter(center); });
        renderParticipants();
        showStatus("장소명이나 주소를 검색해 출발지를 확정해주세요.");
    } catch (error) {
        ui.placeholder.classList.add("failed");
        ui.placeholderText.textContent = error.message;
        showStatus(error.message, true);
    }
}

ui.demo.addEventListener("click", () => {
    if (!map || loading || searching || routesLoading) return;
    if (locations.length && !window.confirm("현재 참가자 위치를 예시 위치 2곳으로 바꿀까요?")) return;
    clearSelection();
    ui.searchResults.replaceChildren();
    ui.searchQuery.value = "";
    ui.searchStatus.textContent = "검색 결과를 선택한 뒤 출발지로 확정하세요.";
    locations = [{ name: "예시 출발지 1", latitude: 37.501, longitude: 127.019, transportMode: "CAR" }, { name: "예시 출발지 2", latitude: 37.509, longitude: 127.039, transportMode: "TRANSIT" }];
    selectedAreaId = null;
    clearResults();
    renderParticipants();
    fitParticipants();
    showStatus("예시 위치 2곳을 넣었어요. 번화가 & 장소 추천 버튼을 눌러주세요.");
});
ui.reset.addEventListener("click", () => {
    if (loading || searching || routesLoading) return;
    clearSelection();
    ui.searchResults.replaceChildren();
    ui.searchQuery.value = "";
    ui.searchStatus.textContent = "검색 결과를 선택한 뒤 출발지로 확정하세요.";
    locations = [];
    selectedAreaId = null;
    clearResults();
    renderParticipants();
    showStatus("위치를 초기화했어요. 출발지를 다시 검색해주세요.");
});
ui.searchForm.addEventListener("submit", searchLocations);
ui.searchQuery.addEventListener("input", updateButtons);
ui.confirm.addEventListener("click", confirmLocation);
ui.category.addEventListener("change", () => {
    clearResults();
    showStatus(`${placeLabel()} 검색으로 바꿨어요. 번화가 & 장소 추천 버튼을 눌러주세요.`);
});
ui.recommend.addEventListener("click", () => recommendCafes());
initialize();
