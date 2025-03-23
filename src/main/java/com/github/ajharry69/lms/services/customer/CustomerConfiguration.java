package com.github.ajharry69.lms.services.customer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CustomerConfiguration {
    @Bean
    public Jaxb2Marshaller kycMarshaller() {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.github.ajharry69.lms.services.customer.integration.wsdl");
        return marshaller;
    }
}