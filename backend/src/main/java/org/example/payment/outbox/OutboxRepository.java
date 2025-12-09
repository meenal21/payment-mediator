package org.example.payment.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {

    List<OutboxMessage> findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    OutboxMessage findTop1ByAggregateTypeAndAggregateIdAndStatusOrderByCreatedAtAsc(
            String aggregateType,
            Long aggregateId,
            OutboxStatus status
    );
}