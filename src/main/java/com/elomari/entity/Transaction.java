package com.elomari.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.time.LocalDateTime;

import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long transactionId;

    private long montant;
    private LocalDateTime transactionDate;
    private LocalDateTime debitDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade({MERGE,PERSIST})
    @JoinColumn(name = "compte_id")
    private Compte compte;



}
