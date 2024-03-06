package io.mintos.banking.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class ExchangeRate {
    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "exchange_rate_sequence",
            sequenceName = "exchange_rate_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "exchange_rate_sequence"
    )
    private Long id;

    @Column
    private String source;

    @Column(precision = 10, scale = 6)
    private BigDecimal rate;
}
