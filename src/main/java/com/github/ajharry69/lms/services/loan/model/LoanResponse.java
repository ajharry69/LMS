package com.github.ajharry69.lms.services.loan.model;

public record LoanResponse(Long loanId, String customerNumber, double amount, LoanStatus status) {

}
