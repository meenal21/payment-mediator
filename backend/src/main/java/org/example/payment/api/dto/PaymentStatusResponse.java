package org.example.payment.api.dto;

import org.example.payment.domain.model.PaymentState;

import java.math.BigDecimal;

public class PaymentStatusResponse {

    private Long id;
    private PaymentState state;
    private BigDecimal amount;
    private BigDecimal taxAmount;

    public PaymentStatusResponse(Long id, PaymentState state,
                                 BigDecimal amount, BigDecimal taxAmount) {
        this.id = id;
        this.state = state;
        this.amount = amount;
        this.taxAmount = taxAmount;
    }

    public Long getId() { return id; }
    public PaymentState getState() { return state; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
}
