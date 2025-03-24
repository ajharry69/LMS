package com.github.ajharry69.lms.services.loan;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/client-registration")
@Slf4j
@Hidden
public class ClientRegistrationController {
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public void clientRegistrationCallback(@RequestBody Map<String, Object> request) {
        log.info("Received request: {}", request);
    }
}
