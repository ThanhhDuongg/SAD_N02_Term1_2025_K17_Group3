package com.example.dorm.model;

/**
 * Defines how a student intends to pay the rent associated with a contract.
 */
public enum PaymentPlan {
    /**
     * The student settles the entire rental fee for the contract duration in a single payment.
     */
    FULL_TERM,

    /**
     * The student pays rent in recurring monthly instalments.
     */
    MONTHLY
}
