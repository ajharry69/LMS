package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatusCode;

public class ClientRegistrationException extends LmsException {
    public ClientRegistrationException(HttpStatusCode statusCode) {
        super(statusCode, "FAILED_TO_REGISTER_CLIENT");
    }
}
