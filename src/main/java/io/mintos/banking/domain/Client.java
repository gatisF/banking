package io.mintos.banking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Client {

    @Id
    @Column(nullable = false, updatable = false)
    @SequenceGenerator(
            name = "client_sequence",
            sequenceName = "client_sequence",
            allocationSize = 1,
            initialValue = 10
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "client_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column()
    private String surname;

    @Column()
    private String personalCode;

    @OneToMany(mappedBy = "client")
    private Set<Account> accounts;

}
