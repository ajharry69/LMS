package com.github.ajharry69.lms.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class ServerUrlProviderImpl implements ServerUrlProvider {
    @Override
    public String getServerUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    }
}
