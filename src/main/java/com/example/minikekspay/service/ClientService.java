package com.example.minikekspay.service;

import com.example.minikekspay.exception.ClientNotFoundException;
import com.example.minikekspay.entity.Client;
import com.example.minikekspay.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClientService {

    ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    public List<Client> getClients() {
        return this.clientRepository.findAll();
    }

    public Client getClient(int id) {
        return this.clientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("The client was not found, ID: " + id) );
    }

    public Client createClient(Client newClient) {
        newClient.setId(0);
        return this.clientRepository.save(newClient);
    }

    public Client updateClient(Client oldClient) {
        this.clientRepository.findById(oldClient.getId()).orElseThrow(() ->
                new ClientNotFoundException("The client was not found and updated, ID: " + oldClient.getId()));

        return this.clientRepository.save(oldClient);
    }

    public int deleteClient(int id) {
        this.clientRepository.findById(id).orElseThrow(() ->
                new ClientNotFoundException("The client was not found and deleted, ID: " + id));

        this.clientRepository.deleteById(id);
        return id;
    }
}
