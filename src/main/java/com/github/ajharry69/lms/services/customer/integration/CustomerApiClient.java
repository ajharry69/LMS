package com.github.ajharry69.lms.services.customer.integration;

import com.github.ajharry69.lms.services.customer.exception.CustomerRetrievalException;
import com.github.ajharry69.lms.services.customer.integration.wsdl.Customer;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerRequest;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Component
@Slf4j
public class CustomerApiClient extends WebServiceGatewaySupport {
    public CustomerApiClient(@Qualifier(value = "customerMarshaller") Jaxb2Marshaller marshaller) {
        setDefaultUri("http://localhost:8080/ws");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }

    public Customer getCustomer(String customerNumber) {
        log.info("Fetching customer details from KYC API for customer number: {}", customerNumber);
        var request = new CustomerRequest();
        request.setCustomerNumber(customerNumber);

        var response = (CustomerResponse) getWebServiceTemplate().marshalSendAndReceive(
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