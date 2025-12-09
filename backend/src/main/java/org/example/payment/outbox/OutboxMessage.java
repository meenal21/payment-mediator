package org.example.payment.outbox;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "outbox_messages")
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType; // e.g. "PAYMENT"
    private Long aggregateId;     // paymentId

    private String messageType;   // "COMMAND" or "EVENT"
    private String messageName;   // e.g. "CALCULATE_TAX"

    @Lob
    private String payload;       // JSON

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private int attemptCount;

    private Instant createdAt;
    private Instant lastAttemptAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        status = OutboxStatus.PENDING;
    }
}
