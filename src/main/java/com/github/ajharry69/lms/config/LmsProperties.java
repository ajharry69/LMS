package com.github.ajharry69.lms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "com.github.ajharry69.lms")
public record LmsProperties(
        String username,
        String password,
        String clientToken
) {
}
