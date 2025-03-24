package com.github.ajharry69.lms.services.loan;

import com.github.ajharry69.lms.services.loan.model.LoanRequest;
import com.github.ajharry69.lms.services.loan.model.LoanResponse;
import com.github.ajharry69.lms.services.loan.model.LoanStatusResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/client-registration")
    public void clientRegistrationCallback(@RequestBody Map<String, Object> request) {
        log.info("Received request: {}", request);
    }

    @PostMapping
    public ResponseEntity<LoanResponse> requestLoan(@Valid @RequestBody LoanRequest request) {
        var response = loanService.requestLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{loanId}/status")
    public ResponseEntity<LoanStatusResponse> getLoanStatus(@PathVariable Long loanId) {
        var response = loanService.getLoanStatus(loanId);
        return ResponseEntity.ok(response);
    }
}
