package org.example.payment.domain.repository;

import org.example.payment.domain.model.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EscrowRepository extends JpaRepository<Escrow, Long> {

    Optional<Escrow> findByPaymentId(Long paymentId);
}
