package com.github.ajharry69.lms.services.customer;

import com.github.ajharry69.lms.services.customer.model.Customer;
import com.github.ajharry69.lms.services.customer.model.CustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> subscribeCustomer(@Valid @RequestBody CustomerRequest request) {
        var customer = customerService.subscribeCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }
}
