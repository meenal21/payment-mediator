package org.example.payment.handler;

import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.EventType;
import org.example.payment.domain.event.PaymentCommand;
import org.example.payment.domain.event.PaymentEvent;
import org.example.payment.domain.model.Payment;
import org.example.payment.domain.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CalculateTaxHandler implements CommandHandler {

    private static final Logger log = LoggerFactory.getLogger(CalculateTaxHandler.class);

    private final PaymentRepository paymentRepository;

    public CalculateTaxHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean supports(String messageName) {
        return CommandType.CALCULATE_TAX.name().equals(messageName);
    }

    @Override
    @Transactional
    public PaymentEvent handle(PaymentCommand command) {
        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found " + command.getPaymentId()));

        // Dummy tax: 10% of amount
        BigDecimal tax = payment.getAmount().multiply(new BigDecimal("0.10"));
        payment.setTaxAmount(tax);
        paymentRepository.save(payment);

        log.info("Calculated dummy tax {} for payment {}", tax, payment.getId());

        return new PaymentEvent(EventType.TAX_CALCULATED, payment.getId());
    }
}
