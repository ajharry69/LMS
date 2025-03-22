package com.github.ajharry69.lms.services.customer.model;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank
        String customerNumber
) {
}
