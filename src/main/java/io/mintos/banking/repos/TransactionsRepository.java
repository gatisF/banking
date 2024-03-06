package io.mintos.banking.repos;

import io.mintos.banking.domain.Transactions;
import io.mintos.banking.records.TransactionsRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    Page<Transactions> findAllByAccountNumber(String accountNumber, Pageable pageable);
    List<TransactionsRecord> findAllByAccountNumberOrderByTimestampDesc(String accountNumber);
}
