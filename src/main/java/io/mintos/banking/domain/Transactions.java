package io.mintos.banking.domain;

import io.mintos.banking.enums.TransactionFlow;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Transactions {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "transaction_sequence",
            sequenceName = "transaction_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "transaction_sequence"
    )
    private Long id;

    @Column
    private String accountNumber;

    @Column
    private BigDecimal balance;

    @Column
    private BigDecimal amount;

    @Column
    private String currency;

    @Column
    private OffsetDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column
    private TransactionFlow transactionFlow;

}
