package com.github.ajharry69.lms.utils.soap;

import com.github.ajharry69.lms.config.LmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.RequestDefaultHeaders;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicHeader;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import java.net.ProxySelector;
import java.net.URI;
import java.util.Base64;
import java.util.List;

import static org.apache.http.auth.AuthScope.ANY_REALM;
import static org.apache.http.auth.AuthScope.ANY_SCHEME;

@Slf4j
public abstract class LmsWebServiceGatewaySupport extends WebServiceGatewaySupport {
    public LmsWebServiceGatewaySupport(
            final LmsProperties lmsProperties,
            Jaxb2Marshaller marshaller,
            String defaultUri
    ) {
        setDefaultUri(defaultUri);
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        setMessageSender(getMessageSender(lmsProperties));
        setInterceptors(new ClientInterceptor[]{new SoapLoggingInterceptor()});
    }

    private static WebServiceMessageSender getMessageSender(LmsProperties lmsProperties) {
        HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender();
        messageSender.setCredentials(new UsernamePasswordCredentials(lmsProperties.username(), lmsProperties.password()));
        return messageSender;
    }

    private static WebServiceMessageSender getMessageSender1(LmsProperties lmsProperties) {
        var authString = lmsProperties.username() + ":" + lmsProperties.password();
        var credentials = Base64.getEncoder().encodeToString(authString.getBytes());
        List<Header> headers = List.of(new BasicHeader("Authorization", "Basic " + credentials));

        HttpClient httpClient = HttpClients.custom()
                .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                .addInterceptorLast(new RequestDefaultHeaders(headers))
                .build();

        return new HttpComponentsMessageSender(httpClient);
    }

    private WebServiceMessageSender getMessageSender2(LmsProperties lmsProperties) {
        var uri = URI.create(getDefaultUri());
        var routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                new AuthScope(uri.getHost(), uri.getPort(), ANY_REALM, ANY_SCHEME),
                new UsernamePasswordCredentials(lmsProperties.username(), lmsProperties.password())
        );

        HttpClient httpclient = HttpClients.custom()
                .setRoutePlanner(routePlanner)
                .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        return new HttpComponentsMessageSender(httpclient);
    }
}