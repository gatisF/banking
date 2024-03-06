package io.mintos.banking.records;

import java.math.BigDecimal;

public record TransfareRecord(String senderAccount, String receiverAccount, BigDecimal amount, String currency) {
}
