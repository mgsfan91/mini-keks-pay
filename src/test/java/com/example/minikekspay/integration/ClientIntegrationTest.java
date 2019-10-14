package com.example.minikekspay.integration;

import com.example.minikekspay.entity.Client;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ClientRepository clientRepository;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void initData() {
        Client client1 = new Client(0, "testerA", 0.0);
        Client client2 = new Client(0, "testerB", 0.0);
        this.clientRepository.save(client1);
        this.clientRepository.save(client2);
    }

    @After
    public void clearData() {
        this.clientRepository.deleteAll();
    }

    @Test
    public void getPresetClients() throws Exception {
        this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("testerA")))
                .andExpect(jsonPath("$[1].name", is("testerB")))
                .andExpect(jsonPath("$[0].saldo", is(0.0)));
    }

    @Test
    public void getExistingClient() throws Exception {

        // given we have a client with some ID
        MvcResult result = this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk()).andReturn();
        List<Client> clients = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Client>>() {});
        Integer id = clients.get(0).getId();

        // then we should retrieve it
        this.mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testerA")))
                .andExpect(jsonPath("$.saldo", is(0.0)));
    }

    @Test
    public void createClient() throws Exception {

        // when we create a new client and send it in the body
        Client newClient = new Client();
        newClient.setName("testerNew");
        newClient.setSaldo(0);
        newClient.setId(0);
        String requestJson = mapper.writer().writeValueAsString(newClient);
        MvcResult result = this.mockMvc.perform(post("/clients")
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk()).andReturn();

        // then it will be created and have an ID assigned
        Client client = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<Client>() {});

        // and we can retrieve it and it will match
        this.mockMvc.perform(get("/clients/" + client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testerNew")))
                .andExpect(jsonPath("$.saldo", is(0.0)));
    }

    @Test
    public void updateClient() throws Exception {

        // given we have a client with some ID
        MvcResult result = this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk()).andReturn();
        List<Client> clients = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Client>>() {});
        Integer id = clients.get(0).getId();

        // when we retrieve it
        MvcResult result2 = this.mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isOk()).andReturn();
        Client client = mapper.readValue(result2.getResponse().getContentAsString(),
                new TypeReference<Client>() {});
        String clientName = client.getName();

        // and we updated it with a different name
        client.setName(clientName + "_modified");
        String requestJson = mapper.writer().writeValueAsString(client);
        this.mockMvc.perform(put("/clients")
                .contentType("application/json")
                .content(requestJson))
                .andExpect(status().isOk());

        // then it should be updated
        this.mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(clientName + "_modified")))
                .andReturn();
    }

    @Test
    public void deleteClient() throws Exception {

        // given we have a client with some ID
        MvcResult result = this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk()).andReturn();
        List<Client> clients = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Client>>() {});
        Integer id = clients.get(0).getId();

        // when we delete it
        this.mockMvc.perform(delete("/clients/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string(id.toString()));

        // then we wont have it no more
        this.mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        containsString("The client was not found, ID: " + id)));
    }

}
