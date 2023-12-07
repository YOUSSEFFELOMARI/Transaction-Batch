package com.elomari.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import static org.hibernate.annotations.CascadeType.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private long compteId;

    private long solde;



    @OneToMany(mappedBy = "compte", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade({MERGE,PERSIST})
    private Set<Transaction> transactionSet;
}
