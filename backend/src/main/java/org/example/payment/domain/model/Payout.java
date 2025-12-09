package org.example.payment.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payouts")
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;
    private Long sellerId;

    @Column(nullable = false)
    private BigDecimal grossAmount;   // captured amount

    @Column(nullable = false)
    private BigDecimal feeAmount;     // platform fee

    @Column(nullable = false)
    private BigDecimal netAmount;     // amount sent to seller

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayoutStatus status;

    private String externalPayoutRef; // bank txn id (later)

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) {
            status = PayoutStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    // getters & setters...
}
