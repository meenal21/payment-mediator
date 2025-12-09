package org.example.payment.api.dto;

import org.example.payment.domain.model.PaymentState;

import java.math.BigDecimal;

public class CheckoutResponse {
    private Long paymentId;
    private PaymentState state;
    private BigDecimal amount;

    public CheckoutResponse(Long paymentId, PaymentState state, BigDecimal amount) {
        this.paymentId = paymentId;
        this.state = state;
        this.amount = amount;
    }

    public Long getPaymentId() { return paymentId; }
    public PaymentState getState() { return state; }
    public BigDecimal getAmount() { return amount; }
}
