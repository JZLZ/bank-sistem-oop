package jzlz.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MoneyAudit(
        UUID transactionId,
        BankService targetService,
        String description,
        long amount, // <<< valor da transação
        OffsetDateTime createdAt
) {
    public MoneyAudit(BankService targetService, String description, long amount) {
        this(UUID.randomUUID(), targetService, description, amount, OffsetDateTime.now());
    }
}
