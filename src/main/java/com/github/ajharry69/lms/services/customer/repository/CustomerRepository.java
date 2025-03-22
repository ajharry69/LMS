package com.github.ajharry69.lms.services.customer.repository;

import com.github.ajharry69.lms.services.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByNumber(String number);
}