package com.github.ajharry69.lms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


@Component
@Slf4j
public class ClientHttpRequestLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (log.isDebugEnabled()) {
            logRequest(request, body);
        }
        ClientHttpResponse response = execution.execute(request, body);
        return logResponse(response);
    }

    private ClientHttpResponse logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.info("Response status: {}", response.getStatusCode());

            logHeaders(response.getHeaders());
        }

        byte[] responseBody = response.getBody().readAllBytes();

        if (log.isDebugEnabled() && responseBody.length > 0) {
            log.info("Response body: {}", new String(responseBody, StandardCharsets.UTF_8));
        }
        return new BufferingClientHttpResponseWrapper(response, responseBody);
    }

    private void logHeaders(HttpHeaders headers) {
        headers.forEach((key, values) -> values.forEach(value -> log.info("{}: {}", key, value)));
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("Request: {}: {}", request.getMethod(), request.getURI());
        logHeaders(request.getHeaders());
        if (body.length > 0) {
            log.info("Body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private record BufferingClientHttpResponseWrapper(
            ClientHttpResponse response,
            byte[] body) implements ClientHttpResponse {
        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }
}
