package org.example.payment.domain.repository;

import org.example.payment.domain.model.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutRepository extends JpaRepository<Payout, Long> {

    Optional<Payout> findByPaymentId(Long paymentId);
}
