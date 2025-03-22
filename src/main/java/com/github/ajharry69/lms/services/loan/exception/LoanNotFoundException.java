package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatus;

public class LoanNotFoundException extends LmsException {
    public LoanNotFoundException() {
        super(HttpStatus.NOT_FOUND, "LOAN_NOT_FOUND");
    }
}
