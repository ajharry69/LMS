package com.github.ajharry69.lms.services.loan.model;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.utils.ServerUrlProvider;

public record RegisterClientRequest(
        String url,
        String name,
        String username,
        String password
) {
    public RegisterClientRequest(ServerUrlProvider urlProvider, LmsProperties properties) {
        this(
                urlProvider.getServerUrl() + "/api/v1/loans/client-registration",
                "LMS",
                properties.username(),
                properties.password()
        );
    }
}