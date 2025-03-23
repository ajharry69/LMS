package com.github.ajharry69.lms.services.loan.model;

public record ClientRegistrationResponse(
        int id,
        String url,
        String name,
        String username,
        String password,
        String token
) {
}
