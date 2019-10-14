package com.example.minikekspay.service;

import com.example.minikekspay.entity.Client;
import com.example.minikekspay.entity.Transaction;
import com.example.minikekspay.exception.ClientNotFoundException;
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

        addAmountToClients(trxEntry.getSourcesIds(), trxEntry.getAmount());
        deductAmountFromClients(trxEntry.getDestinationsIds(), trxEntry.getAmount());

        trxPreProcess.setProcessed(true);
        return this.transactionRepository.save(trxPreProcess);
    }

    public Transaction submitGain(TransactionEntry trxEntry) {

        Transaction trxInit = new Transaction(TransactionTypes.GAIN, trxEntry);
        Transaction trxPreProcess = this.transactionRepository.save(trxInit);

        deductAmountFromClients(trxEntry.getSourcesIds(), trxEntry.getAmount());
        addAmountToClients(trxEntry.getDestinationsIds(), trxEntry.getAmount());

        trxPreProcess.setProcessed(true);
        return this.transactionRepository.save(trxPreProcess);
    }


    @Transactional
    void addAmountToClients(Integer[] clientsIds, double amount) {
        double singleAmount = amount / clientsIds.length;
        for (Integer clientId: clientsIds) {
            Client client = this.clientRepository.findById(clientId).orElseThrow(() ->
                    new ClientNotFoundException("Missing a client while adding amounts, ID: " + clientId));
            double saldo = client.getSaldo();
            client.setSaldo(saldo + singleAmount);
            this.clientRepository.save(client);
        }
    }

    @Transactional
    void deductAmountFromClients(Integer[] clientsIds, double amount) {
        double singleAmount = amount / clientsIds.length;
        for (Integer clientId: clientsIds) {
            Client client = this.clientRepository.findById(clientId).orElseThrow(() ->
                    new ClientNotFoundException("Missing a client while deducting amounts, ID: " + clientId));
            double saldo = client.getSaldo();
            client.setSaldo(saldo - singleAmount);
            this.clientRepository.save(client);
        }
    }
}
