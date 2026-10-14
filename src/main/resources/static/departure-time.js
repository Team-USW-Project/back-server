"use strict";

// 브라우저가 해외 시간대를 사용해도 입력과 표시를 한국 시간으로 처리합니다.
(function (root) {
    const MINUTE = 60 * 1000;
    const KST = 9 * 60 * MINUTE;

    function parseAppointment(value) {
        const match = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})$/.exec(value);
        if (!match) return null;
        const [, year, month, day, hour, minute] = match.map(Number);
        if (year < 1000 || month < 1 || month > 12 || day < 1 || hour > 23 || minute > 59) return null;
        const utc = Date.UTC(year, month - 1, day, hour, minute);
        const date = new Date(utc);
        // Date가 2월 30일 같은 입력을 다음 달로 자동 보정하는 것을 방지합니다.
        if (date.getUTCFullYear() !== year || date.getUTCMonth() !== month - 1 || date.getUTCDate() !== day) return null;
        return utc - KST;
    }

    function minimumAppointment(now = Date.now()) {
        const date = new Date(Math.ceil((now + 1) / MINUTE) * MINUTE + KST);
        return date.toISOString().slice(0, 16);
    }

    function calculate(value, durationSeconds, bufferMinutes, now = Date.now()) {
        const appointment = parseAppointment(value);
        if (appointment === null) return {status: "INVALID_TIME"};
        if (appointment <= now) return {status: "PAST_APPOINTMENT"};
        if (![0, 10, 20].includes(bufferMinutes)) return {status: "INVALID_BUFFER"};
        if (!Number.isFinite(durationSeconds) || durationSeconds < 0) return {status: "NO_DURATION"};
        // 화면의 '약 N분'과 맞추고 늦지 않도록 초 단위를 분으로 올림합니다.
        const duration = Math.ceil(durationSeconds / 60) * MINUTE;
        const arrival = appointment - bufferMinutes * MINUTE;
        const departure = arrival - duration;
        return {status: departure < now ? "DEPARTURE_PASSED" : "OK", departure, arrival};
    }

    function formatTime(timestamp) {
        return new Intl.DateTimeFormat("ko-KR", {
            timeZone: "Asia/Seoul", month: "numeric", day: "numeric",
            hour: "2-digit", minute: "2-digit", hour12: false
        }).format(new Date(timestamp));
    }

    const api = {parseAppointment, minimumAppointment, calculate, formatTime};
    if (typeof module !== "undefined" && module.exports) module.exports = api;
    else root.DepartureSchedule = api;
})(globalThis);
