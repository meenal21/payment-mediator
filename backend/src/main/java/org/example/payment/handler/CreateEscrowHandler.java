package org.example.payment.handler;

import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.EventType;
import org.example.payment.domain.event.PaymentCommand;
import org.example.payment.domain.event.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateEscrowHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CreateEscrowHandler.class);

    @Override
    public boolean supports(String messageName) {
        return CommandType.CREATE_ESCROW.name().equals(messageName);
    }

    @Override
    @Transactional
    public PaymentEvent handle(PaymentCommand command) {
        Long paymentId = command.getPaymentId();
        log.info("Dummy escrow created for payment {}", paymentId);
        // later: create Escrow entity and save

        return new PaymentEvent(EventType.ESCROW_CREATED, paymentId);
    }
}
