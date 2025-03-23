package com.github.ajharry69.lms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Slf4j
public class SoapLoggingInterceptor implements ClientInterceptor {
    enum MessageType {
        Request,
        Response,
        Fault,
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        logSoapMessage(MessageType.Request, messageContext);
        return true; // Continue processing
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        logSoapMessage(MessageType.Response, messageContext);
        return true; // Continue processing
    }

    @Override
    public boolean handleFault(MessageContext messageContext) {
        logSoapMessage(MessageType.Fault, messageContext);
        return true; // Continue processing
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) {
        // No action needed here, but you can add cleanup if required
    }

    private void logSoapMessage(MessageType type, MessageContext messageContext) {
        if (log.isDebugEnabled()) {
            SaajSoapMessage soapMessage;
            if (type == MessageType.Request) {
                soapMessage = (SaajSoapMessage) messageContext.getRequest();
            } else {
                soapMessage = (SaajSoapMessage) messageContext.getResponse();
            }
            var buffer = new ByteArrayOutputStream();
            try {
                soapMessage.writeTo(buffer);
            } catch (IOException e) {
                log.error("Failed to log SOAP request", e);
            }
            String soapMessageString = buffer.toString(StandardCharsets.UTF_8);
            log.debug("SOAP {} : \n{}", type, soapMessageString);
        }
    }
}