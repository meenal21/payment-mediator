package org.example.payment.api.dto;

public class PayRequest {
    private String paymentMethod; // "UPI", "CARD", etc. (just for UI)

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
