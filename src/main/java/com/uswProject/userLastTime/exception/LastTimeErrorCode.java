package com.uswProject.userLastTime.exception;

import com.uswProject.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LastTimeErrorCode implements ErrorCode {

    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "경로가 없습니다."),
    BUS_NOT_CORRECT(HttpStatus.BAD_REQUEST, "버스 ID가 일치하지 않습니다."),
    BUS_STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "버스 정류장을 찾을 수 없습니다."),
    SUBWAY_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "지하철 노선을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    LastTimeErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
