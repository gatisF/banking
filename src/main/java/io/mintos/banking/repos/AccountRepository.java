package io.mintos.banking.repos;

import io.mintos.banking.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);
    Account getAccountByAccountNumber(String accountNumber);
}
