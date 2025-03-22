package com.github.ajharry69.lms.services.customer.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatus;

public class CustomerRetrievalException extends LmsException {
    public CustomerRetrievalException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FAILED_TO_FETCH_CUSTOMER_DETAILS");
    }
}