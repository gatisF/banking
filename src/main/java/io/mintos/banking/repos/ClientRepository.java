package io.mintos.banking.repos;

import io.mintos.banking.domain.Client;
import io.mintos.banking.records.AccountRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT new io.mintos.banking.records.AccountRecord(A.accountNumber, A.balance, A.currency) FROM Client C INNER JOIN Account A ON C.id = A.client.id WHERE C.personalCode = :personalCode")
    List<AccountRecord> findClientByPersonalCode(@Param("personalCode") String personalCode);

    boolean existsByPersonalCode(String personalCode);
}
