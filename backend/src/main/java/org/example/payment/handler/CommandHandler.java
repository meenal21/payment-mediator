package org.example.payment.handler;

import org.example.payment.domain.event.PaymentCommand;
import org.example.payment.domain.event.PaymentEvent;

public interface CommandHandler {
    boolean supports(String messageName);

    PaymentEvent handle(PaymentCommand command);
}
