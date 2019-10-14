package com.example.minikekspay.integration;

import com.example.minikekspay.entity.Client;
import com.example.minikekspay.model.TransactionEntry;
import com.example.minikekspay.repository.ClientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ClientRepository clientRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void initData() {
        Client client1 = new Client(0, "testerA", 0.0);
        Client client2 = new Client(0, "testerB", 0.0);
        Client client3 = new Client(0, "testerC", 0.0);
        this.clientRepository.save(client1);
        this.clientRepository.save(client2);
        this.clientRepository.save(client3);
    }

    @After
    public void clearData() {
        this.clientRepository.deleteAll();
    }

    @Test
    public void shouldHaveCorrectValues() throws Exception {

        // given we have clients with IDs
        MvcResult result = this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk()).andReturn();
        List<Client> clients = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Client>>() {});
        int id0 = clients.get(0).getId();
        int id1 = clients.get(1).getId();
        int id2 = clients.get(2).getId();

        // and we add some transactions
        // A borrows (it is cost from his perspective) B 100
        TransactionEntry entry0 = new TransactionEntry(100, new Integer[] {id0}, new Integer[] {id1});
        String postJson0 = mapper.writer().writeValueAsString(entry0);
        this.mockMvc.perform(post("/transactions/cost")
                .contentType("application/json")
                .content(postJson0))
                .andExpect(status().isOk());

        // B borrows C 70
        TransactionEntry entry1 = new TransactionEntry(70, new Integer[] {id1}, new Integer[] {id2});
        String postJson1 = mapper.writer().writeValueAsString(entry1);
        this.mockMvc.perform(post("/transactions/cost")
                .contentType("application/json")
                .content(postJson1))
                .andExpect(status().isOk());

        // C borrows A 120
        TransactionEntry entry2 = new TransactionEntry(120, new Integer[] {id2}, new Integer[] {id0});
        String postJson2 = mapper.writer().writeValueAsString(entry2);
        this.mockMvc.perform(post("/transactions/cost")
                .contentType("application/json")
                .content(postJson2))
                .andExpect(status().isOk());

        // B pays 63 for A, B, C
        TransactionEntry entry3 = new TransactionEntry(63, new Integer[] {id1}, new Integer[] {id0, id1, id2});
        String postJson3 = mapper.writer().writeValueAsString(entry3);
        this.mockMvc.perform(post("/transactions/cost")
                .contentType("application/json")
                .content(postJson3))
                .andExpect(status().isOk());

        // then the situation should be: A: -41, B: 12, C: 29
        this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("testerA")))
                .andExpect(jsonPath("$[0].saldo", is(-41.0)))
                .andExpect(jsonPath("$[1].saldo", is(12.0)))
                .andExpect(jsonPath("$[2].saldo", is(29.0)));
    }
}
