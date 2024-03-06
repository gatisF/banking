package io.mintos.banking.services;

import io.mintos.banking.records.AccountRecord;
import io.mintos.banking.records.TransactionsRecord;
import io.mintos.banking.records.TransfareRecord;
import java.util.List;

public interface BankingService {
    List<AccountRecord> getAccountsByClient(String personalCode);
    List<TransactionsRecord> getTransactionHistory(String accountId, Integer offset, Integer limit);
    void transferFunds(TransfareRecord transfareRecord);

    boolean clientExists(String personalCode);
}
