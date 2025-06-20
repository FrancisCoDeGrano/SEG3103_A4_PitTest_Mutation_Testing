package com.banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final TransactionType type;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, BigDecimal amount, String description, LocalDateTime timestamp) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s: %s %.2f - %s",
                timestamp.toString(), type, amount, description);
    }
}