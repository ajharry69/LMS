package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatus;

public class TransactionRetrievalException extends LmsException {
    public TransactionRetrievalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FAILED_TO_FETCH_TRANSACTIONS");
    }
}