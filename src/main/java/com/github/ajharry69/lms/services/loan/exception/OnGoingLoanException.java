package com.github.ajharry69.lms.services.loan.exception;

import com.github.ajharry69.lms.exception.LmsException;
import org.springframework.http.HttpStatus;

public class OnGoingLoanException extends LmsException {
    public OnGoingLoanException() {
        super(HttpStatus.PRECONDITION_FAILED, "ONGOING_LOAN_REQUEST_FAILED");
    }
}
