package org.example.payment.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(
        name = "idempotency_records",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_idem_key_endpoint",
                columnNames = {"idempotencyKey", "endpoint"}
        )
)
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Provided by client (header or request body)
    @Column(nullable = false, length = 100)
    private String idempotencyKey;

    // Logical endpoint / operation, e.g. "CHECKOUT", "DELIVER"
    @Column(nullable = false, length = 100)
    private String endpoint;

    // What resource did this call create/affect?
    private String resourceType;  // e.g. "PAYMENT"
    private Long resourceId;      // e.g. paymentId

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdempotencyStatus status;

    private Instant createdAt;
    private Instant lastSeenAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        lastSeenAt = createdAt;
        if (status == null) {
            status = IdempotencyStatus.COMPLETED;
        }
    }

    @PreUpdate
    public void onUpdate() {
        lastSeenAt = Instant.now();
    }

    // getters & setters...
}
