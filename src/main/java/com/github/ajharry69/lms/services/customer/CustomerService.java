package com.github.ajharry69.lms.services.customer;

import com.github.ajharry69.lms.services.customer.integration.KycApiClient;
import com.github.ajharry69.lms.services.customer.model.Customer;
import com.github.ajharry69.lms.services.customer.model.CustomerRequest;
import com.github.ajharry69.lms.services.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final KycApiClient kycApiClient;
    private final CustomerRepository repository;

    @Transactional
    public Customer subscribeCustomer(final CustomerRequest request) {
        log.info("Subscribing customer with number: {}", request.customerNumber());
        return repository.findByNumber(request.customerNumber()).orElseGet(
                () -> {
                    var kycCustomer = kycApiClient.getCustomer(request.customerNumber());

                    var customer = repository.save(Customer.builder()
                            .number(kycCustomer.getCustomerNumber())
                            .firstName(kycCustomer.getFirstName())
                            .lastName(kycCustomer.getLastName())
                            .build());
                    log.info("Customer subscribed successfully: {}", customer.getNumber());
                    return customer;
                }
        );
    }
}