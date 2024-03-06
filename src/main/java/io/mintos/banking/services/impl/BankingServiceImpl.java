package io.mintos.banking.services.impl;

import io.mintos.banking.domain.Account;
import io.mintos.banking.domain.Transactions;
import io.mintos.banking.enums.Currency;
import io.mintos.banking.enums.TransactionFlow;
import io.mintos.banking.exception.AccountEqualException;
import io.mintos.banking.exception.AmountExceedsException;
import io.mintos.banking.exception.CurrencyNotSupportedException;
import io.mintos.banking.records.AccountRecord;
import io.mintos.banking.records.TransactionsRecord;
import io.mintos.banking.records.TransfareRecord;
import io.mintos.banking.repos.AccountRepository;
import io.mintos.banking.repos.ClientRepository;
import io.mintos.banking.repos.TransactionsRepository;
import io.mintos.banking.services.BankingService;
import io.mintos.banking.utils.CurrencyConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BankingServiceImpl implements BankingService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionsRepository transactionsRepository;

    private final CurrencyConverter currencyConverter;

    public BankingServiceImpl(ClientRepository clientRepository, AccountRepository accountRepository,
                              TransactionsRepository transactionsRepository, CurrencyConverter currencyConverter) {
        this.clientRepository = Objects.requireNonNull(clientRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.transactionsRepository = Objects.requireNonNull(transactionsRepository);
        this.currencyConverter = Objects.requireNonNull(currencyConverter);
    }

    @Override
    public List<AccountRecord> getAccountsByClient(String personalCode) {
        return clientRepository.findClientByPersonalCode(personalCode);
    }

    @Override
    public List<TransactionsRecord> getTransactionHistory(String accountNumber, Integer offset, Integer limit) {
        if (Objects.isNull(offset) || Objects.isNull(limit)) {
            return transactionsRepository.findAllByAccountNumberOrderByTimestampDesc(accountNumber);
        }
        Pageable pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Transactions> allByAccountNumber = transactionsRepository.findAllByAccountNumber(accountNumber, pageable);
        return allByAccountNumber.stream()
                .map(t -> new TransactionsRecord(t.getAccountNumber(), t.getBalance(), t.getAmount(),
                        t.getCurrency(), t.getTimestamp(), t.getTransactionFlow()))
                .toList();
    }

    @Override
    public void transferFunds(TransfareRecord transfareRecord) {
        BigDecimal amount;
        validateAccount(transfareRecord.senderAccount(), transfareRecord.receiverAccount());
        validateCurrency(transfareRecord.currency());
        Account senderAccount = accountRepository.getAccountByAccountNumber(transfareRecord.senderAccount());
        Account receiverAccount = accountRepository.getAccountByAccountNumber(transfareRecord.receiverAccount());
        if (!transfareRecord.currency().equalsIgnoreCase(receiverAccount.getCurrency()))
            throw new CurrencyNotSupportedException("Transfer currency does not match receiver currency!");
        else {
            amount = convert(transfareRecord, senderAccount);
        }

        validateAmount(transfareRecord, senderAccount);

        senderAccount.setBalance(senderAccount.getBalance().subtract(transfareRecord.amount()));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
        transactionsRepository.save(new Transactions()
                .setAccountNumber(senderAccount.getAccountNumber())
                .setCurrency(senderAccount.getCurrency())
                .setBalance(senderAccount.getBalance())
                .setAmount(transfareRecord.amount())
                .setTransactionFlow(TransactionFlow.OUTGOING)
                .setTimestamp(OffsetDateTime.now()));

        transactionsRepository.save(new Transactions()
                .setAccountNumber(receiverAccount.getAccountNumber())
                .setCurrency(receiverAccount.getCurrency())
                .setBalance(receiverAccount.getBalance())
                .setAmount(amount)
                .setTransactionFlow(TransactionFlow.INCOMING)
                .setTimestamp(OffsetDateTime.now()));
    }

    void validateCurrency(String currency) {
        if (!Currency.isValid(currency))
            throw new CurrencyNotSupportedException("Provided currency is not supported!");
    }

    void validateAccount(String senderAccount, String receiverAccount) {
        if(senderAccount.equalsIgnoreCase(receiverAccount))
            throw new AccountEqualException("Sender and receiver accounts must differ!");
    }

    BigDecimal convert(TransfareRecord transfareRecord, Account senderAccount) {
        if (transfareRecord.currency().equalsIgnoreCase(senderAccount.getCurrency())) {
            return transfareRecord.amount();
        } else {
            return currencyConverter.convert(senderAccount.getCurrency(), transfareRecord.currency(), transfareRecord.amount());
        }
    }

    void validateAmount(TransfareRecord transfareRecord, Account senderAccount) {
        if (transfareRecord.amount().compareTo(senderAccount.getBalance()) > 0)
            throw new AmountExceedsException("Given amount exceeds available funds!");
    }

    @Override
    public boolean clientExists(String personalCode) {
        return clientRepository.existsByPersonalCode(personalCode);
    }
}
