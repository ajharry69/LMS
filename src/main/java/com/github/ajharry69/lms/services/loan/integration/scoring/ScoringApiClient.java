package com.github.ajharry69.lms.services.loan.integration.scoring;

import com.github.ajharry69.lms.services.loan.exception.ClientRegistrationException;
import com.github.ajharry69.lms.services.loan.exception.InitiateQueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.QueryScoreException;
import com.github.ajharry69.lms.services.loan.exception.SendTransactionException;
import com.github.ajharry69.lms.services.loan.model.ScoringResponse;
import com.github.ajharry69.lms.services.loan.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoringApiClient {
    private static final String SCORING_ENGINE_BASE_URL = "https://scoringtest.credable.io/api/v1/scoring";
    private static final String CLIENT_TOKEN = "your-client-token"; // TODO: Replace with actual client token
    private final RestTemplate restTemplate;

    private static HttpEntity<String> getHttpEntity() {
        var headers = new HttpHeaders();
        headers.set("client-token", CLIENT_TOKEN);
        return new HttpEntity<>(headers);
    }

    private String initiateQueryScore(String customerNumber) {
        log.info("Initiating query score for customer: {}", customerNumber);

        var entity = getHttpEntity();

        var response = restTemplate.exchange(
                SCORING_ENGINE_BASE_URL + "/initiateQueryScore/" + customerNumber,
                HttpMethod.GET,
                entity,
                String.class
        );

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

        var entity = getHttpEntity();

        var response = restTemplate.exchange(
                SCORING_ENGINE_BASE_URL + "/queryScore/" + token,
                HttpMethod.GET,
                entity,
                ScoringResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to query score. Status code: {}", response.getStatusCode());
            throw new QueryScoreException(response.getStatusCode());
        }

        var scoringResponse = response.getBody();
        log.info("Successfully retrieved score: {}", scoringResponse.getScore());
        return scoringResponse;
    }

    public ScoringResponse getScore(String customerNumber, List<Transaction> transactions) {
        var clientToken = registerClient();

        sendTransactionData(transactions, clientToken);

        var token = initiateQueryScore(customerNumber);

        return queryScore(token);
    }

    private String registerClient() {
        log.info("Registering client with Scoring Engine.");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //language=JSON
        var requestBody = """
                {
                  "url": "http://localhost:8080/api/v1/transactions",
                  "name": "LMS Service",
                  "username": "admin",
                  "password": "password"
                }""";

        var entity = new HttpEntity<>(requestBody, headers);

        var response = restTemplate.exchange(
                SCORING_ENGINE_BASE_URL + "/client/createClient",
                HttpMethod.POST,
                entity,
                ScoringResponse.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to register client. Status code: {}", response.getStatusCode());
            throw new ClientRegistrationException(response.getStatusCode());
        }

        var clientResponse = response.getBody();
        log.info("Client registered successfully.  Received token: {}", clientResponse.getCustomerNumber());
        return clientResponse.getCustomerNumber();
    }

    private void sendTransactionData(List<Transaction> transactions, String clientToken) {
        log.info("Sending transaction data to Scoring Engine.");

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("client-token", clientToken);

        var entity = new HttpEntity<>(transactions, headers);

        var response = restTemplate.exchange(
                "http://localhost:8080/api/v1/transactions",
                HttpMethod.POST,
                entity,
                Void.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to send transaction data. Status code: {}", response.getStatusCode());
            throw new SendTransactionException(response.getStatusCode());
        }

        log.info("Transaction data sent successfully.");
    }
}