package com.example.minikekspay.aspect;

import com.example.minikekspay.exception.ClientNotFoundException;
import com.example.minikekspay.model.TransactionEntry;
import com.example.minikekspay.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class AssertBeforeTransaction {

    @Autowired
    ClientRepository clientRepository;


    @Before("submitPointcut()")
    public void assertClients(JoinPoint joinPoint) {

        TransactionEntry entry = (TransactionEntry) joinPoint.getArgs()[0];
        String sources = Arrays.toString(entry.getSources());
        String destinations = Arrays.toString(entry.getDestinations());
        log.info("Assert amount " + entry.getAmount() + " from " + sources + " to " + destinations);

        for (Integer clientId: entry.getSources()) {
            this.clientRepository.findById(clientId).orElseThrow(() ->
                    new ClientNotFoundException("Missing a source client, ID: " + clientId));
        }
        for (Integer clientId: entry.getDestinations()) {
            this.clientRepository.findById(clientId).orElseThrow(() ->
                    new ClientNotFoundException("Missing a destination client, ID: " + clientId));
        }
    }

    @Pointcut("execution(* com.example.minikekspay.service.TransactionService.submit*(..))")
    public void submitPointcut(){}
}
