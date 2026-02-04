package com.uswProject.userLastTime.exception;

import com.uswProject.global.error.core.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum LastTimeErrorCode implements ErrorCode {

    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "경로가 없습니다.");

    private final HttpStatus status;
    private final String message;

    LastTimeErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
