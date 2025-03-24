package com.github.ajharry69.lms.services.loan.integration;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.services.loan.exception.ClientRegistrationException;
import com.github.ajharry69.lms.services.loan.model.ClientRegistration;
import com.github.ajharry69.lms.services.loan.model.ClientRegistrationResponse;
import com.github.ajharry69.lms.services.loan.model.RegisterClientRequest;
import com.github.ajharry69.lms.services.loan.repository.ClientRegistrationRepository;
import com.github.ajharry69.lms.utils.ClientHttpRequestLoggingInterceptor;
import com.github.ajharry69.lms.utils.ServerUrlProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class ClientRegistrationApiClient {
    protected static final String CLIENT_TOKEN_HEADER = "client-token";
    protected final RestClient restClient;
    protected final LmsProperties properties;
    protected final ServerUrlProvider serverUrlProvider;
    protected final ClientRegistrationRepository registrationRepository;

    public ClientRegistrationApiClient(
            LmsProperties properties,
            ServerUrlProvider serverUrlProvider,
            RestClient.Builder restClientBuilder,
            ClientRegistrationRepository registrationRepository,
            ClientHttpRequestLoggingInterceptor clientHttpRequestLoggingInterceptor
    ) {
        this.properties = properties;
        this.serverUrlProvider = serverUrlProvider;
        this.registrationRepository = registrationRepository;
        this.restClient = restClientBuilder.baseUrl("https://scoringtest.credable.io/api/v1")
                .requestInterceptor(clientHttpRequestLoggingInterceptor)
                .build();
    }

    public ClientRegistration registerClient() {
        var request = new RegisterClientRequest(serverUrlProvider, properties);
        return registrationRepository.findByUrl(request.url()).orElseGet(() -> {
            log.info("Registering client with Scoring Engine.");

            var response = restClient.post()
                    .uri("/client/createClient")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(CLIENT_TOKEN_HEADER, properties.clientToken())
                    .body(request)
                    .retrieve()
                    .toEntity(ClientRegistrationResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to register client. Status code: {}", response.getStatusCode());
                throw new ClientRegistrationException(response.getStatusCode());
            }

            var clientResponse = response.getBody();
            log.info("Client registered successfully. Response: {}", clientResponse);
            return new ClientRegistration(
                    (long) clientResponse.id(),
                    clientResponse.url(),
                    clientResponse.name(),
                    clientResponse.username(),
                    clientResponse.password(),
                    clientResponse.token()
            );
        });
    }
}