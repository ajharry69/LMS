package com.github.ajharry69.lms.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public class LmsException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String errorCode;

    public LmsException(HttpStatus httpStatus, String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public LmsException(HttpStatusCode httpStatus, String errorCode) {
        this(HttpStatus.resolve(httpStatus.value()), errorCode);
    }
}

