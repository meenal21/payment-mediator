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
public class ExecutePayoutHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ExecutePayoutHandler.class);

    @Override
    public boolean supports(String messageName) {
        return CommandType.EXECUTE_PAYOUT.name().equals(messageName);
    }

    @Override
    @Transactional
    public PaymentEvent handle(PaymentCommand command) {
        Long paymentId = command.getPaymentId();
        log.info("Dummy payout executed for payment {}", paymentId);
        // real: call bank / payout API

        return new PaymentEvent(EventType.PAYOUT_SUCCEEDED, paymentId);
    }
}
