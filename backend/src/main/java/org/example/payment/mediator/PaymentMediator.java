package org.example.payment.mediator;

import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.EventType;
import org.example.payment.domain.event.PaymentEvent;
import org.example.payment.domain.model.Payment;
import org.example.payment.domain.model.PaymentState;
import org.example.payment.domain.repository.PaymentRepository;
import org.example.payment.outbox.OutboxService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentMediator {
    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;

    public PaymentMediator(PaymentRepository paymentRepository, OutboxService outboxService) {
        this.paymentRepository = paymentRepository;
        this.outboxService = outboxService;
    }

    @Transactional
    public Payment startCheckout(Payment payment) {
        payment.setState(PaymentState.CREATED);
        Payment saved = paymentRepository.save(payment);

        // First step: calculate tax
        outboxService.enqueueCommand(saved.getId(), CommandType.CALCULATE_TAX);

        return saved;
    }

    @Transactional
    public void handleEvent(PaymentEvent event) {
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found " + event.getPaymentId()));

        EventType type = event.getType();
        switch (type) {
            case TAX_CALCULATED -> {
                payment.setState(PaymentState.TAXED);
                paymentRepository.save(payment);

                // NEXT STEP: ask to authorize payment
//                outboxService.enqueueCommand(payment.getId(), CommandType.AUTHORIZE_PAYMENT);
            }

            case AUTHORIZATION_SUCCEEDED -> {
                payment.setState(PaymentState.AUTHORIZED);
                paymentRepository.save(payment);

                // NEXT STEP: create escrow
                outboxService.enqueueCommand(payment.getId(), CommandType.CREATE_ESCROW);
            }

            case ESCROW_CREATED -> {
                payment.setState(PaymentState.ESCROW_CREATED);
                paymentRepository.save(payment);
                // Next step will be triggered when item is delivered
            }

            case ITEM_DELIVERED -> {
                payment.setState(PaymentState.DELIVERED);
                paymentRepository.save(payment);

                // After delivery, capture funds
                outboxService.enqueueCommand(payment.getId(), CommandType.CAPTURE_PAYMENT);
            }

            case CAPTURE_SUCCEEDED -> {
                payment.setState(PaymentState.CAPTURED);
                paymentRepository.save(payment);

                outboxService.enqueueCommand(payment.getId(), CommandType.COMPUTE_PAYOUT);
            }

            case PAYOUT_COMPUTED -> {
                outboxService.enqueueCommand(payment.getId(), CommandType.EXECUTE_PAYOUT);
            }

            case PAYOUT_SUCCEEDED -> {
                payment.setState(PaymentState.PAYOUT_COMPLETED);
                paymentRepository.save(payment);
            }
            default -> {
                // no-op or logging
            }
        }

        // For this minimal version: no further commands here.
    }
}
