package org.example.payment.mediator;

import org.example.payment.api.dto.CheckoutRequest;
import org.example.payment.api.dto.CheckoutResponse;
import org.example.payment.api.dto.PayRequest;
import org.example.payment.api.dto.PaymentStatusResponse;
import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.EventType;
import org.example.payment.domain.event.PaymentEvent;
import org.example.payment.domain.model.Payment;
import org.example.payment.domain.model.PaymentState;
import org.example.payment.domain.repository.PaymentRepository;
import org.example.payment.domain.service.IdempotencyService;
import org.example.payment.outbox.OutboxProcessor;
import org.example.payment.outbox.OutboxService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentMediator paymentMediator;
    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;
    private final OutboxService outboxService;
    private final OutboxProcessor outboxProcessor;

    public PaymentService(PaymentMediator paymentMediator,
                          PaymentRepository paymentRepository,
                          IdempotencyService idempotencyService,
                          OutboxService outboxService,
                          OutboxProcessor outboxProcessor) {
        this.paymentMediator = paymentMediator;
        this.paymentRepository = paymentRepository;
        this.idempotencyService = idempotencyService;
        this.outboxService = outboxService;
        this.outboxProcessor = outboxProcessor;
    }

    @Transactional
    public CheckoutResponse createCheckout(CheckoutRequest request) {
        String idempotencyKey = request.getIdempotencyKey();
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey is required for checkout");
        }

        // Either returns existing paymentId for this key+endpoint,
        // or runs the lambda to create a new Payment + start the workflow.
        Long paymentId = idempotencyService.execute(
                idempotencyKey,
                "CHECKOUT",   // logical endpoint name
                "PAYMENT",    // resource type
                () -> {
                    Payment payment = new Payment();
                    payment.setBuyerId(request.getBuyerId());
                    payment.setSellerId(request.getSellerId());
                    payment.setAmount(request.getAmount());

                    Payment saved = paymentMediator.startCheckout(payment);
                    return saved.getId();
                }
        );

        Payment saved = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalStateException("Payment not found after idempotent checkout"));

        return new CheckoutResponse(saved.getId(), saved.getState(), saved.getAmount());
    }

    @Transactional(readOnly = true)
    public PaymentStatusResponse getStatus(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        return new PaymentStatusResponse(
                payment.getId(),
                payment.getState(),
                payment.getAmount(),
                payment.getTaxAmount()
        );
    }

    @Transactional
    public PaymentStatusResponse simulateGatewaySuccess(Long paymentId, PayRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getState() != PaymentState.TAXED) {
            throw new IllegalStateException("Payment must be TAXED before gateway success");
        }

        // Optionally store selected payment method (if Payment has such a field)
        // payment.setPaymentMethod(request.getPaymentMethod());
        // paymentRepository.save(payment);

        // Enqueue AUTHORIZE_PAYMENT command
        outboxService.enqueueCommand(paymentId, CommandType.AUTHORIZE_PAYMENT);

        // Process AUTHORIZE + CREATE_ESCROW chain
        outboxProcessor.processForPayment(paymentId);

        Payment updated = paymentRepository.findById(paymentId)
                .orElseThrow();

        return new PaymentStatusResponse(
                updated.getId(),
                updated.getState(),
                updated.getAmount(),
                updated.getTaxAmount()
        );
    }

    @Transactional
    public PaymentStatusResponse markDelivered(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        if (payment.getState() != PaymentState.ESCROW_CREATED) {
            throw new IllegalStateException("Payment must be ESCROW_CREATED before delivery");
        }

        // External business event
        PaymentEvent delivered = new PaymentEvent(EventType.ITEM_DELIVERED, paymentId);
        paymentMediator.handleEvent(delivered);

        // Process CAPTURE + payout chain
        outboxProcessor.processForPayment(paymentId);

        Payment updated = paymentRepository.findById(paymentId)
                .orElseThrow();

        return new PaymentStatusResponse(
                updated.getId(),
                updated.getState(),
                updated.getAmount(),
                updated.getTaxAmount()
        );
    }


}
