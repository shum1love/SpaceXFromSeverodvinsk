package org.example.spacecompany.domain.finance;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Неизменяемая запись об одном движении денег.
 *
 * <p>Суммы всегда хранятся положительными; направление задаёт {@link TransactionType}.
 * {@link BigDecimal} (а не {@code double}) — чтобы копейки не терялись
 * в ошибках округления с плавающей точкой.
 */
public final class Transaction {

    private final UUID id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, BigDecimal amount, String description) {
        this(UUID.randomUUID(), type, amount, description, LocalDateTime.now());
    }

    public Transaction(UUID id, TransactionType type, BigDecimal amount,
                       String description, LocalDateTime timestamp) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Сумма не может быть отрицательной");
        }
        this.amount = amount;
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание не должно быть пустым");
        }
        this.description = description.strip();
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
    }

    public UUID getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** Signed amount: positive for income, negative for expenses. */
    public BigDecimal signedAmount() {
        return type == TransactionType.INCOME ? amount : amount.negate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        String sign = type == TransactionType.INCOME ? "+" : "-";
        return "[%s] %s%s %s (%s)".formatted(timestamp, sign, amount, description,
                type == TransactionType.INCOME ? "доход" : "расход");
    }
}
