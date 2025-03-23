package com.github.ajharry69.lms.services.loan.integration;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.services.loan.exception.ClientRegistrationException;
import com.github.ajharry69.lms.services.loan.exception.InitiateQueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.QueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.SendTransactionException;
import com.github.ajharry69.lms.services.loan.model.ClientRegistrationResponse;
import com.github.ajharry69.lms.services.loan.model.RegisterClientRequest;
import com.github.ajharry69.lms.services.loan.model.ScoringResponse;
import com.github.ajharry69.lms.services.loan.model.Transaction;
import com.github.ajharry69.lms.utils.ServerUrlProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class ScoringApiClient {
    private static final String CLIENT_TOKEN_HEADER = "client-token";
    private final RestClient restClient;
    private final LmsProperties properties;
    private final ServerUrlProvider serverUrlProvider;

    public ScoringApiClient(
            RestClient.Builder restClientBuilder,
            LmsProperties properties,
            ServerUrlProvider serverUrlProvider
    ) {
        this.restClient = restClientBuilder.baseUrl("https://scoringtest.credable.io/api/v1/scoring")
                .build();
        this.properties = properties;
        this.serverUrlProvider = serverUrlProvider;
    }

    private ClientRegistrationResponse registerClient() {
        log.info("Registering client with Scoring Engine.");

        var request = new RegisterClientRequest(serverUrlProvider, properties);
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
        log.info("Client registered successfully.  Received token: {}", clientResponse);
        return clientResponse;
    }

    private String getQueryScoreToken(String customerNumber) {
        log.info("Initiating query score for customer: {}", customerNumber);

        var response = restClient.get()
                .uri("/initiateQueryScore/" + customerNumber)
                .header(CLIENT_TOKEN_HEADER, properties.clientToken())
                .retrieve()
                .toEntity(String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to initiate query score. Status code: {}", response.getStatusCode());
            throw new InitiateQueryScoreException(response.getStatusCode());
        }

        String token = response.getBody();
        log.info("Successfully initiated query score. Received token: {}", token);
        return token;
    }

    @Retryable(
            retryFor = {QueryScoreException.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private ScoringResponse queryScore(String token) {
        log.info("Querying score for token: {}", token);

        var response = restClient.get()
                .uri("/queryScore/" + token)
                .header(CLIENT_TOKEN_HEADER, properties.clientToken())
                .retrieve()
                .toEntity(ScoringResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to query score. Status code: {}", response.getStatusCode());
            throw new QueryScoreException(response.getStatusCode());
        }

        var scoringResponse = response.getBody();
        log.info("Successfully retrieved score: {}", scoringResponse.score());
        return scoringResponse;
    }

    private void sendTransactionData(
            ClientRegistrationResponse clientRegistrationResponse,
            List<Transaction> transactions
    ) {
        log.info("Sending transaction data to Scoring Engine.");

        var response = restClient.post()
                .uri(clientRegistrationResponse.url())
                .header(CLIENT_TOKEN_HEADER, clientRegistrationResponse.token())
                .body(transactions)
                .retrieve()
                .toBodilessEntity();

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to send transaction data. Status code: {}", response.getStatusCode());
            throw new SendTransactionException(response.getStatusCode());
        }

        log.info("Transaction data sent successfully.");
    }

    public ScoringResponse getScore(String customerNumber, List<Transaction> transactions) {
        var registerClientResponse = registerClient();

        sendTransactionData(registerClientResponse, transactions);

        var token = getQueryScoreToken(customerNumber);

        return queryScore(token);
    }
}