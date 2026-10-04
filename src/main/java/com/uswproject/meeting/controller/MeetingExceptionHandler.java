package com.uswproject.meeting.controller;

import com.uswproject.kakao.exception.KakaoApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {MeetingController.class, LocationSearchController.class})
public class MeetingExceptionHandler {

    @ExceptionHandler(KakaoApiException.class)
    public ResponseEntity<ApiError> handleKakaoApiException(KakaoApiException exception) {
        return ResponseEntity.status(exception.getStatus())
                .body(new ApiError(exception.getCode(), exception.getMessage()));
    }

    public record ApiError(String code, String message) {
    }
}
