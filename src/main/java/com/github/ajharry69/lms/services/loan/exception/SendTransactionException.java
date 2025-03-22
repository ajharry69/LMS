package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatusCode;

public class SendTransactionException extends LmsException {
    public SendTransactionException(HttpStatusCode statusCode) {
        super(statusCode, "FAILED_TO_SEND_TRANSACTION_DATA");
    }
}
