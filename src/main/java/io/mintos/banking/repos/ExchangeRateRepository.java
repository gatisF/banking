package io.mintos.banking.repos;

import io.mintos.banking.domain.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
    ExchangeRate getExchangeRateBySource(String source);
}
