package com.example.minikekspay.model;

import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class TransactionEntry {

    private double amount;
    private Integer[] sources;
    private Integer[] destinations;
}
