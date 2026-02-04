package com.uswProject.userLastTime.exception;

import com.uswProject.global.error.core.BaseException;

public class LastTimeException extends BaseException {
    public LastTimeException(LastTimeErrorCode errorCode) {
        super(errorCode);
    }
}
