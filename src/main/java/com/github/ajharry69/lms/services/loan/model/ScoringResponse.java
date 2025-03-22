package com.github.ajharry69.lms.services.loan.model;

import lombok.Data;

@Data
public class ScoringResponse {
    private int id;
    private String customerNumber;
    private int score;
    private double limitAmount;
    private String exclusion;
    private String exclusionReason;
}