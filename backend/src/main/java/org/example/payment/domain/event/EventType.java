package org.example.payment.domain.event;

public enum EventType {
    // Tax
    TAX_CALCULATED,
    TAX_CALCULATION_FAILED,

    // Authorization
    AUTHORIZATION_SUCCEEDED,
    AUTHORIZATION_FAILED,

    // Escrow
    ESCROW_CREATED,
    ESCROW_RELEASED,

    // Delivery (usually triggered by API but still modeled as event)
    ITEM_DELIVERED,

    // Capture
    CAPTURE_SUCCEEDED,
    CAPTURE_FAILED,

    // Payout
    PAYOUT_COMPUTED,
    PAYOUT_SUCCEEDED,
    PAYOUT_FAILED
}

