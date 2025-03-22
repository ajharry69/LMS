package com.github.ajharry69.lms.services.loan.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record LoanRequest(
        @NotBlank
        String customerNumber,
        @Positive
        double amount
) {
}