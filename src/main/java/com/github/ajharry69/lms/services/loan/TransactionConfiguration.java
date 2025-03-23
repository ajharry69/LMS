package com.github.ajharry69.lms.services.loan;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@Slf4j
public class TransactionConfiguration {
    @Bean
    public Jaxb2Marshaller transactionMarshaller() {
        var marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.github.ajharry69.lms.services.loan.integration.transaction.wsdl");
        return marshaller;
    }
}