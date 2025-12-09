package org.example.payment.domain.service;

import org.example.payment.domain.model.IdempotencyRecord;
import org.example.payment.domain.model.IdempotencyStatus;
import org.example.payment.domain.repository.IdempotencyRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository repository;

    public IdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    /**
     * Executes an operation in an idempotent way.
     *
     * @param idempotencyKey client-provided unique key
     * @param endpoint logical endpoint name, e.g. "CHECKOUT"
     * @param resourceType e.g. "PAYMENT"
     * @param action code that creates the resource and returns its ID
     * @return existing or newly created resource ID
     */
    @Transactional
    public Long execute(String idempotencyKey,
                        String endpoint,
                        String resourceType,
                        Supplier<Long> action) {

        return repository.findByIdempotencyKeyAndEndpoint(idempotencyKey, endpoint)
                .map(IdempotencyRecord::getResourceId)
                .orElseGet(() -> {
                    Long resourceId = action.get();

                    IdempotencyRecord record = new IdempotencyRecord();
                    record.setIdempotencyKey(idempotencyKey);
                    record.setEndpoint(endpoint);
                    record.setResourceType(resourceType);
                    record.setResourceId(resourceId);
                    record.setStatus(IdempotencyStatus.COMPLETED);

                    repository.save(record);
                    return resourceId;
                });
    }
}
