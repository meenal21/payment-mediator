package org.example.payment.domain.repository;

import org.example.payment.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByPaymentId(Long paymentId);
}
