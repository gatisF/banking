package io.mintos.banking.records;

import io.mintos.banking.enums.TransactionFlow;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionsRecord(String accountNumber, BigDecimal balance, BigDecimal amount, String currency,
                                 OffsetDateTime timestamp, TransactionFlow transactionFlow) {
}
