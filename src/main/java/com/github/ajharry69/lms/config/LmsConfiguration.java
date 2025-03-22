package com.github.ajharry69.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
@EnableRetry
public class LmsConfiguration {

    private static WebServiceTemplate getWebServiceTemplate(String contextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);

        return new WebServiceTemplate(marshaller, marshaller);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public WebServiceTemplate kycWebServiceTemplate() {
        return getWebServiceTemplate("com.github.ajharry69.lms.services.customer.integration.wsdl");
    }

    @Bean
    public WebServiceTemplate transactionWebServiceTemplate() {
        return getWebServiceTemplate("com.github.ajharry69.lms.services.loan.integration.transaction.wsdl");
    }
}