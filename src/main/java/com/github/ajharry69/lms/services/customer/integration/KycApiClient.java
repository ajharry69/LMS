package com.github.ajharry69.lms.services.customer.integration;

import com.github.ajharry69.lms.services.customer.exception.CustomerRetrievalException;
import com.github.ajharry69.lms.services.customer.integration.wsdl.Customer;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerRequest;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Component
@Slf4j
public class KycApiClient {
    private final WebServiceTemplate webServiceTemplate;

    public KycApiClient(@Qualifier(value = "kycWebServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    public Customer getCustomer(String customerNumber) {
        log.info("Fetching customer details from KYC API for customer number: {}", customerNumber);
        var request = new CustomerRequest();
        request.setCustomerNumber(customerNumber);

        var response = (CustomerResponse) webServiceTemplate.marshalSendAndReceive(
                "https://kycapitest.credable.io/service/customerWsdl.wsdl",
                request,
                new SoapActionCallback("https://kyc.credable.com/CustomerRequest")
        );

        if (response == null || response.getCustomer() == null) {
            log.error("Failed to retrieve customer details from KYC API.");
            throw new CustomerRetrievalException();
        }
        log.info("Successfully retrieved customer details from KYC API.");
        return response.getCustomer();
    }
}