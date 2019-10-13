package com.example.minikekspay.controller;

import com.example.minikekspay.entity.Client;
import com.example.minikekspay.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }


    @GetMapping
    public List<Client> getClients() {
        return this.clientService.getClients();
    }

    @GetMapping("/{id}")
    public Client getClient(@PathVariable int id) {
        return this.clientService.getClient(id);
    }

    @PostMapping
    public Client create(@RequestBody Client newClient) {
        return this.clientService.createClient(newClient);
    }

    @PutMapping
    public Client update(@RequestBody Client oldClient) {
        return this.clientService.updateClient(oldClient);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable int id) {
        return this.clientService.deleteClient(id);
    }
}
