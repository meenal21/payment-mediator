package org.example.payment.domain.event;

public class PaymentEvent {

    private EventType type;
    private Long paymentId;

    public PaymentEvent() {}

    public PaymentEvent(EventType type, Long paymentId) {
        this.type = type;
        this.paymentId = paymentId;
    }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
}