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
public class PayoutComputeHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(PayoutComputeHandler.class);

    @Override
    public boolean supports(String messageName) {
        return CommandType.COMPUTE_PAYOUT.name().equals(messageName);
    }

    @Override
    @Transactional
    public PaymentEvent handle(PaymentCommand command) {
        Long paymentId = command.getPaymentId();
        log.info("Dummy payout computed for payment {}", paymentId);
        // later: compute actual fee, net amount, store Payout

        return new PaymentEvent(EventType.PAYOUT_COMPUTED, paymentId);
    }
}
