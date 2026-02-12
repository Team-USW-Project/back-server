package com.uswProject.userLastTime.exception;

import com.uswProject.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LastTimeSuccessCode implements SuccessCode {

    LAST_TIME_ROUTE_SUCCESS_SEARCH_ROUTE(HttpStatus.OK, "경로 찾기에 성공하였습니다."),
    LAST_TIME_SUCCESS_CODE(HttpStatus.OK, "막차 시간을 성공적으로 찾았습니다.");

    private final HttpStatus status;
    private final String message;

    LastTimeSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
