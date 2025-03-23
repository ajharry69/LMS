package com.github.ajharry69.lms.services.loan.model;

import org.springframework.lang.NonNull;

public record ScoringResponse(
        int id,
        @NonNull String customerNumber,
        int score,
        double limitAmount,
        String exclusion,
        String exclusionReason
) {
}