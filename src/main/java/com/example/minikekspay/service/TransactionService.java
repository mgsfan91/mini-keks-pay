package com.example.minikekspay.service;

import com.example.minikekspay.entity.Transaction;
import com.example.minikekspay.model.TransactionEntry;
import com.example.minikekspay.model.TransactionTypes;
import com.example.minikekspay.repository.ClientRepository;
import com.example.minikekspay.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TransactionService {

    ClientRepository clientRepository;
    TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }


    public Transaction submitCost(TransactionEntry trxEntry) {

        Transaction trxInit = new Transaction(TransactionTypes.COST, trxEntry);
        Transaction trxPreProcess = this.transactionRepository.save(trxInit);

        modifyClients(trxEntry.getSources(), trxEntry.getAmount(), true);
        modifyClients(trxEntry.getDestinations(), trxEntry.getAmount(), false);

        trxPreProcess.setProcessed(true);
        return this.transactionRepository.save(trxPreProcess);
    }

    public Transaction submitGain(TransactionEntry trxEntry) {

        Transaction trxInit = new Transaction(TransactionTypes.GAIN, trxEntry);
        Transaction trxPreProcess = this.transactionRepository.save(trxInit);

        modifyClients(trxEntry.getSources(), trxEntry.getAmount(), false);
        modifyClients(trxEntry.getDestinations(), trxEntry.getAmount(), true);

        trxPreProcess.setProcessed(true);
        return this.transactionRepository.save(trxPreProcess);
    }

    @Transactional
    void modifyClients(Integer[] clientsIds, double amount, boolean addFlag) {
        double singleAmount = amount / clientsIds.length;
        for (Integer clientId: clientsIds) {
            this.clientRepository.findById(clientId).ifPresent(client -> {
                double saldo = client.getSaldo();
                if (addFlag) {
                    client.setSaldo(saldo + singleAmount);
                }
                else {
                    client.setSaldo(saldo - singleAmount);
                }
                this.clientRepository.save(client);
            });
        }
    }
}
