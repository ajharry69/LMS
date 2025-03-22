package com.github.ajharry69.lms.services.loan.model;

import com.github.ajharry69.lms.services.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Customer customer;
    private double amount;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    private int score;
    private double limitAmount;
}