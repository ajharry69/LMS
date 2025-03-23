package com.github.ajharry69.lms.services.customer.integration;

import com.github.ajharry69.lms.services.customer.exception.CustomerRetrievalException;
import com.github.ajharry69.lms.services.customer.integration.wsdl.Customer;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerRequest;
import com.github.ajharry69.lms.services.customer.integration.wsdl.CustomerResponse;
import com.github.ajharry69.lms.utils.SoapLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.client.core.SoapActionCallback;

@Component
@Slf4j
public class KYCApiClient extends WebServiceGatewaySupport {
    public KYCApiClient(@Qualifier(value = "kycMarshaller") Jaxb2Marshaller marshaller) {
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        ClientInterceptor[] interceptors = new ClientInterceptor[] {
                new SoapLoggingInterceptor()
        };
        setInterceptors(interceptors);
    }

    public Customer getCustomer(String customerNumber) {
        log.info("Fetching customer details from KYC API for customer number: {}", customerNumber);
        var request = new CustomerRequest();
        request.setCustomerNumber(customerNumber);

        var response = (CustomerResponse) getWebServiceTemplate().marshalSendAndReceive(
                "https://kycapitest.credable.io/service/customerWsdl.wsdl",
                request,
                new SoapActionCallback("")
        );

        if (response == null || response.getCustomer() == null) {
            log.error("Failed to retrieve customer details from KYC API.");
            throw new CustomerRetrievalException();
        }
        log.info("Successfully retrieved customer details from KYC API.");
        return response.getCustomer();
    }
}