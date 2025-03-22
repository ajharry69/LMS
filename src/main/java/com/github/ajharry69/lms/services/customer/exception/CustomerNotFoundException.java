package com.github.ajharry69.lms.services.customer.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatus;

public class CustomerNotFoundException extends LmsException {
    public CustomerNotFoundException() {
        super(HttpStatus.NOT_FOUND, "CUSTOMER_NOT_FOUND");
    }
}
