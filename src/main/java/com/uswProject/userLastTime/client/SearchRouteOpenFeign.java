package com.uswProject.userLastTime.client;

import com.uswProject.global.config.OdsayFeignConfig;
import com.uswProject.global.config.OpenFeignConfig;
import com.uswProject.userLastTime.dto.BusResponse;
import com.uswProject.userLastTime.dto.OdsayResponse;
import com.uswProject.userLastTime.dto.SubwayResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * name : 클라이언트 이름
 * url : application.yml에 지정한 값을 가져온다.
 * configuration : OdsayFeignConfig를 통해 인터셉터 설정 연결
 */
@FeignClient(
        name = "SearchRouteOpenFeign",
        url = "${odsay.api.base-url}",
        configuration = {OdsayFeignConfig.class, OpenFeignConfig.class})
public interface SearchRouteOpenFeign {

    @GetMapping("/searchPubTransPathT")
    OdsayResponse searchPath(
            @RequestParam("SX") double startX,
            @RequestParam("SY") double startY,
            @RequestParam("EX") double endX,
            @RequestParam("EY") double endY
    );

    @GetMapping("/busLaneDetail")
    BusResponse searchBus(
            @RequestParam("busID") int busID
    );

    @GetMapping("/searchSubwaySchedule")
    SubwayResponse searchSubway(
            @RequestParam("stationID") int stationID
    );


}
