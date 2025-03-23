package com.github.ajharry69.lms.utils.soap;

import com.github.ajharry69.lms.config.LmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Map;

@Slf4j
public abstract class LmsWebServiceGatewaySupport extends WebServiceGatewaySupport {
    public LmsWebServiceGatewaySupport(
            final LmsProperties lmsProperties,
            Jaxb2Marshaller marshaller
    ) {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        ClientInterceptor[] interceptors = new ClientInterceptor[]{
                new SoapLoggingInterceptor()
        };
        setInterceptors(interceptors);
        // Set credentials for Basic Authentication in the SOAP request
        var messageSender = new HttpUrlConnectionMessageSender() {
            @Override
            protected void prepareConnection(HttpURLConnection connection) throws IOException {
                super.prepareConnection(connection);

                var authString = lmsProperties.username() + ":" + lmsProperties.password();
                var credentials = Base64.getEncoder().encodeToString(authString.getBytes());
                log.debug("Auth credentials: {}", credentials);
                connection.setRequestProperty("Authorization", "Basic " + credentials);
                /*var authenticator = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(lmsProperties.username(), lmsProperties.password().toCharArray());
                    }
                };
                connection.setAuthenticator(authenticator);*/
                log.debug("Request Headers:");
                for (Map.Entry<String, java.util.List<String>> header : connection.getRequestProperties().entrySet()) {
                    log.debug("{}: {}", header.getKey(), String.join(", ", header.getValue()));
                }
            }
        };
        setMessageSender(messageSender);
    }
}
