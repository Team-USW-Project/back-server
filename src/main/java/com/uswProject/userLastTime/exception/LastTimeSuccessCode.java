package com.uswProject.userLastTime.exception;

import com.uswProject.global.success.SuccessCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LastTimeSuccessCode implements SuccessCode {

    Last_TIME_SUCCESS_SEARCH_ROUTE(HttpStatus.OK, "경로 찾기에 성공하였습니다.");

    private final HttpStatus status;
    private final String message;

    LastTimeSuccessCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
