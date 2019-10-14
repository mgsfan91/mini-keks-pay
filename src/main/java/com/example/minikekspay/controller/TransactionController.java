package com.example.minikekspay.controller;

import com.example.minikekspay.model.TransactionEntry;
import com.example.minikekspay.entity.Transaction;
import com.example.minikekspay.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transactions")
public class TransactionController {

    TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/cost")
    public Transaction cost(@RequestBody TransactionEntry trxEntry) {
        return this.transactionService.submitCost(trxEntry);
    }

    @PostMapping("/gain")
    public Transaction gain(@RequestBody TransactionEntry trxEntry) {
        return this.transactionService.submitGain(trxEntry);
    }
}
