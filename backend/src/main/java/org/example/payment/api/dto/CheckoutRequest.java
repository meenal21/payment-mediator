package org.example.payment.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CheckoutRequest {
    private Long buyerId;
    private Long sellerId;
    private BigDecimal amount;
    private String idempotencyKey;
}
