package org.example.payment.api;

import org.example.payment.domain.model.Payment;
import org.example.payment.domain.repository.PaymentRepository;
import org.example.payment.outbox.OutboxMessage;
import org.example.payment.outbox.OutboxRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;

    public DebugController(PaymentRepository paymentRepository,
                           OutboxRepository outboxRepository) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
    }

    @GetMapping("/payments")
    public List<Payment> allPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping("/outbox")
    public List<OutboxMessage> allOutbox() {
        return outboxRepository.findAll();
    }
}
