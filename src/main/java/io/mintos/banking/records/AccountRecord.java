package io.mintos.banking.records;

import java.math.BigDecimal;

public record AccountRecord(String accountNumber, BigDecimal balance, String currency) {
}
