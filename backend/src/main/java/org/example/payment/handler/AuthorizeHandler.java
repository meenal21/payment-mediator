package org.example.payment.handler;

import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.EventType;
import org.example.payment.domain.event.PaymentCommand;
import org.example.payment.domain.event.PaymentEvent;
import org.example.payment.domain.model.Payment;
import org.example.payment.domain.model.PaymentState;
import org.example.payment.domain.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuthorizeHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthorizeHandler.class);

    private final PaymentRepository paymentRepository;

    public AuthorizeHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean supports(String messageName) {
        return CommandType.AUTHORIZE_PAYMENT.name().equals(messageName);
    }

    @Override
    @Transactional
    public PaymentEvent handle(PaymentCommand command) {
        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found " + command.getPaymentId()));

        if (payment.getState() == PaymentState.AUTHORIZED || payment.getState() == PaymentState.ESCROW_CREATED) {
            log.info("Payment {} already authorized, skipping dummy auth", payment.getId());
            return new PaymentEvent(EventType.AUTHORIZATION_SUCCEEDED, payment.getId());
        }

        log.info("Dummy authorization for payment {}", payment.getId());
        // In real life, call gateway here

        return new PaymentEvent(EventType.AUTHORIZATION_SUCCEEDED, payment.getId());
    }
}
