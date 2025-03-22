package com.github.ajharry69.lms.services.loan.repository;

import com.github.ajharry69.lms.services.customer.model.Customer;
import com.github.ajharry69.lms.services.loan.model.Loan;
import com.github.ajharry69.lms.services.loan.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    boolean existsByCustomerAndStatusNot(Customer customer, LoanStatus status);
}