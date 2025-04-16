package com.github.ajharry69.lms.services.loan.integration;

import com.github.ajharry69.lms.config.LmsProperties;
import com.github.ajharry69.lms.services.loan.exception.InitiateQueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.QueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.SendTransactionException;
import com.github.ajharry69.lms.services.loan.model.ClientRegistration;
import com.github.ajharry69.lms.services.loan.model.ScoringResponse;
import com.github.ajharry69.lms.services.loan.model.Transaction;
import com.github.ajharry69.lms.services.loan.repository.ClientRegistrationRepository;
import com.github.ajharry69.lms.utils.ClientHttpRequestLoggingInterceptor;
import com.github.ajharry69.lms.utils.ServerUrlProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class ScoringApiClient extends ClientRegistrationApiClient {
    public ScoringApiClient(
            LmsProperties properties,
            ServerUrlProvider serverUrlProvider,
            RestClient.Builder restClientBuilder,
            ClientRegistrationRepository registrationRepository,
            ClientHttpRequestLoggingInterceptor clientHttpRequestLoggingInterceptor
    ) {
        super(
                properties,
                serverUrlProvider,
                restClientBuilder,
                registrationRepository,
                clientHttpRequestLoggingInterceptor
        );
    }

    private String getQueryScoreToken(String customerNumber) {
        log.info("Initiating query score for customer: {}", customerNumber);

        var registerClientResponse = registerClient();

        var response = restClient.get()
                .uri("/scoring/initiateQueryScore/" + customerNumber)
                .header(CLIENT_TOKEN_HEADER, registerClientResponse.getToken())
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

        var registerClientResponse = registerClient();

        var response = restClient.get()
                .uri("/scoring/queryScore/" + token)
                .header(CLIENT_TOKEN_HEADER, registerClientResponse.getToken())
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
            ClientRegistration clientRegistration,
            List<Transaction> transactions
    ) {
        log.info("Sending transaction data to Scoring Engine.");

        var response = restClient.post()
                .uri(clientRegistration.getUrl())
                .header(CLIENT_TOKEN_HEADER, clientRegistration.getToken())
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