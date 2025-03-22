package com.github.ajharry69.lms.services.customer.model;

import com.github.ajharry69.lms.services.loan.model.Loan;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "customer")
    private List<Loan> loans;
}