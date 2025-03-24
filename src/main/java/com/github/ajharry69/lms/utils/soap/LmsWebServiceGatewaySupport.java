package com.github.ajharry69.lms.utils.soap;

import com.github.ajharry69.lms.config.LmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

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
}