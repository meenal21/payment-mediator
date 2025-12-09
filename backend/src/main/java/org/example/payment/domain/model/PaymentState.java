package org.example.payment.domain.model;

public enum PaymentState {
    CREATED,
    TAXED,
    FAILED,
    AUTHORIZED,
    ESCROW_CREATED,
    CAPTURED,
    DELIVERED,
    DELIVERY_FAILED,
    PAYOUT_COMPLETED
}