package org.example.payment.domain.event;

public class PaymentCommand {

    private CommandType type;
    private Long paymentId;

    public PaymentCommand() {}

    public PaymentCommand(CommandType type, Long paymentId) {
        this.type = type;
        this.paymentId = paymentId;
    }

    public CommandType getType() { return type; }
    public void setType(CommandType type) { this.type = type; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
}