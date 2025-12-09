package org.example.payment.outbox;

import org.example.payment.domain.event.PaymentCommand;
import org.example.payment.domain.event.PaymentEvent;
import org.example.payment.handler.CommandHandler;
import org.example.payment.mediator.PaymentMediator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final OutboxRepository outboxRepository;
    private final List<CommandHandler> commandHandlers;
    private final ObjectMapper objectMapper;
    private final PaymentMediator paymentMediator;

    public OutboxProcessor(OutboxRepository outboxRepository,
                           List<CommandHandler> commandHandlers,
                           ObjectMapper objectMapper,
                           PaymentMediator paymentMediator) {
        this.outboxRepository = outboxRepository;
        this.commandHandlers = commandHandlers;
        this.objectMapper = objectMapper;
        this.paymentMediator = paymentMediator;
    }

    @Scheduled(fixedDelay = 3000) // every 3 seconds
    public void processOutbox() {
        List<OutboxMessage> messages =
                outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxMessage message : messages) {
            try {
                handleMessage(message);
            } catch (Exception e) {
                log.error("Failed to process outbox message {}", message.getId(), e);
                message.setStatus(OutboxStatus.FAILED);
                message.setAttemptCount(message.getAttemptCount() + 1);
                message.setLastAttemptAt(Instant.now());
                outboxRepository.save(message);
            }
        }
    }

    @Transactional
    protected void handleMessage(OutboxMessage message) throws Exception {
        message.setStatus(OutboxStatus.PROCESSING);
        message.setLastAttemptAt(Instant.now());
        outboxRepository.save(message);

        if ("COMMAND".equals(message.getMessageType())) {
            PaymentCommand command =
                    objectMapper.readValue(message.getPayload(), PaymentCommand.class);

            CommandHandler handler = commandHandlers.stream()
                    .filter(h -> h.supports(message.getMessageName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No handler for " + message.getMessageName()));

            PaymentEvent event = handler.handle(command);

            // Give the event to mediator
            paymentMediator.handleEvent(event);
        }

        message.setStatus(OutboxStatus.PROCESSED);
        outboxRepository.save(message);
    }

    public void processForPayment(Long paymentId) {
        boolean processed;
        do {
            processed = processSingleForPayment(paymentId);
        } while (processed);
    }

    private boolean processSingleForPayment(Long paymentId) {
        OutboxMessage message = outboxRepository
                .findTop1ByAggregateTypeAndAggregateIdAndStatusOrderByCreatedAtAsc(
                        "PAYMENT",
                        paymentId,
                        OutboxStatus.PENDING
                );

        if (message == null) {
            return false;
        }
        try {
            handleMessage(message);
        } catch (Exception e) {
            log.error("Failed to process outbox message {}", message.getId(), e);
            message.setStatus(OutboxStatus.FAILED);
            message.setAttemptCount(message.getAttemptCount() + 1);
            message.setLastAttemptAt(Instant.now());
            outboxRepository.save(message);
        }
        return true;
    }
}
