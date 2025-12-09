package org.example.payment.domain.event;

public enum CommandType {
    // Tax calculation
    CALCULATE_TAX,

    // Payment gateway
    AUTHORIZE_PAYMENT,
    CAPTURE_PAYMENT,

    // Escrow lifecycle
    CREATE_ESCROW,
    RELEASE_ESCROW,

    // Seller payout
    COMPUTE_PAYOUT,
    EXECUTE_PAYOUT
}
