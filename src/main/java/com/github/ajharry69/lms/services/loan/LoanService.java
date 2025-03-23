package com.github.ajharry69.lms.services.loan;

import com.github.ajharry69.lms.services.customer.exception.CustomerNotFoundException;
import com.github.ajharry69.lms.services.customer.repository.CustomerRepository;
import com.github.ajharry69.lms.services.loan.exception.LoanNotFoundException;
import com.github.ajharry69.lms.services.loan.exception.OnGoingLoanException;
import com.github.ajharry69.lms.services.loan.integration.ScoringApiClient;
import com.github.ajharry69.lms.services.loan.integration.TransactionApiClient;
import com.github.ajharry69.lms.services.loan.model.*;
import com.github.ajharry69.lms.services.loan.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final TransactionApiClient transactionApiClient;
    private final ScoringApiClient scoringApiClient;

    @Transactional
    public LoanResponse requestLoan(LoanRequest request) {
        log.info("Requesting loan for customer: {}, amount: {}", request.customerNumber(), request.amount());

        var customer = customerRepository.findByNumber(request.customerNumber())
                .orElseThrow(CustomerNotFoundException::new);

        if (loanRepository.existsByCustomerAndStatusNot(customer, LoanStatus.REJECTED)) {
            log.error("Customer has an ongoing loan request that has already been rejected");
            throw new OnGoingLoanException();
        }

        var transactions = transactionApiClient.getTransactions(request.customerNumber());

        // Send transaction data to the Scoring Engine and get the score.
        var scoringResponse = scoringApiClient.getScore(request.customerNumber(), transactions);

        var loanStatus = determineLoanStatus(
                scoringResponse.score(),
                scoringResponse.limitAmount(),
                request.amount()
        );

        var loan = loanRepository.save(
                Loan.builder()
                        .customer(customer)
                        .amount(request.amount())
                        .status(loanStatus)
                        .score(scoringResponse.score())
                        .limitAmount(scoringResponse.limitAmount())
                        .build()
        );

        log.info("Loan request processed. Loan status: {}", loanStatus);
        return new LoanResponse(loan.getId(), loan.getCustomer().getNumber(), loan.getAmount(), loan.getStatus());
    }

    public LoanStatusResponse getLoanStatus(Long loanId) {
        log.info("Getting loan status for loan ID: {}", loanId);
        var loan = loanRepository.findById(loanId)
                .orElseThrow(LoanNotFoundException::new);
        return new LoanStatusResponse(loan.getStatus());
    }

    private LoanStatus determineLoanStatus(int score, double limitAmount, double amount) {
        return score >= 600 && amount <= limitAmount ? LoanStatus.APPROVED : LoanStatus.REJECTED;
    }
}