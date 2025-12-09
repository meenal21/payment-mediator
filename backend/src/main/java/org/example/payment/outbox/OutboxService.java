package org.example.payment.outbox;

import org.example.payment.domain.event.CommandType;
import org.example.payment.domain.event.PaymentCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Service
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void enqueueCommand(Long paymentId, CommandType type) {
        try {
            PaymentCommand command = new PaymentCommand(type, paymentId);
            String payload = objectMapper.writeValueAsString(command);

            OutboxMessage message = new OutboxMessage();
            message.setAggregateType("PAYMENT");
            message.setAggregateId(paymentId);
            message.setMessageType("COMMAND");
            message.setMessageName(type.name());
            message.setPayload(payload);

            outboxRepository.save(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enqueue command", e);
        }
    }
}
