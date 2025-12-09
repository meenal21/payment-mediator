package org.example.payment.domain.model;

public enum EscrowStatus {
    HELD,        // funds locked
    RELEASED,    // moved to seller or back to buyer
    CANCELLED
}
