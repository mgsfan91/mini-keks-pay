package com.example.minikekspay.entity;

import com.example.minikekspay.model.TransactionEntry;
import com.example.minikekspay.model.TransactionTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Arrays;


@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 4)
    @Enumerated(EnumType.STRING)
    private TransactionTypes type;

    @Column
    private double amount;

    @Column
    private String sources;

    @Column
    private String destinations;

    @Column
    private boolean processed;


    public Transaction(TransactionTypes type, TransactionEntry trxEntry) {
        this.type = type;
        this.amount = trxEntry.getAmount();
        this.sources = Arrays.toString(trxEntry.getSources());
        this.destinations = Arrays.toString(trxEntry.getDestinations());
        this.processed = false;
    }
}
