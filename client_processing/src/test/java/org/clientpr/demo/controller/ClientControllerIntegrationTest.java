package org.clientpr.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.clientpr.demo.config.TestAopConfig;
import org.clientpr.demo.config.TestSecurityConfig;
import org.clientpr.demo.model.dto.ClientDTO;
import org.clientpr.demo.model.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({TestSecurityConfig.class, TestAopConfig.class})
class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ClientDTO buildClient() {
        return ClientDTO.builder()
                .clientId("770100000002")
                .userId(10L)
                .firstName("Иван")
                .middleName("Иванович")
                .lastName("Иванов")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .documentType(DocumentType.PASSPORT)
                .documentId("123456")
                .documentPrefix("AB")
                .documentSuffix("77")
                .clientRole("CURRENT_CLIENT")
                .build();
    }

    @Test
    void createClient_ShouldReturnCreatedClient() throws Exception {
        ClientDTO input = buildClient();

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.clientId").value("770100000002"))
                .andExpect(jsonPath("$.lastName").value("Иванов"));
    }

    @Test
    void getAllClients_ShouldReturnList() throws Exception {
        // Сначала создаем клиента
        ClientDTO input = buildClient();

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());

        // Затем получаем список
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].clientId").exists());
    }

    @Test
    void getClientById_ShouldReturnClient() throws Exception {
        // Создаем клиента и получаем его ID из ответа
        ClientDTO input = buildClient();

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Извлекаем ID из ответа
        ClientDTO createdClient = objectMapper.readValue(response, ClientDTO.class);
        Long clientId = createdClient.getId();

        // Получаем клиента по ID
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("770100000001"))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }

    @Test
    void updateClient_ShouldReturnUpdatedClient() throws Exception {
        // Создаем клиента
        ClientDTO input = buildClient();

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ClientDTO createdClient = objectMapper.readValue(response, ClientDTO.class);
        Long clientId = createdClient.getId();

        // Обновляем клиента
        ClientDTO updateInput = buildClient();
        updateInput.setFirstName("Петр");
        updateInput.setLastName("Петров");

        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andExpect(jsonPath("$.lastName").value("Петров"));
    }

    @Test
    void deleteClient_ShouldReturnNoContent() throws Exception {
        // Создаем клиента
        ClientDTO input = buildClient();

        String response = mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ClientDTO createdClient = objectMapper.readValue(response, ClientDTO.class);
        Long clientId = createdClient.getId();

        // Удаляем клиента
        mockMvc.perform(delete("/api/clients/{id}", clientId))
                .andExpect(status().isNoContent());

        // Проверяем, что клиент удален
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClientByClientId_ShouldReturnClient() throws Exception {
        // Создаем клиента
        ClientDTO input = buildClient();

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());

        // Ищем по clientId
        mockMvc.perform(get("/api/clients/client-id/{clientId}", "770100000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("770100000001"))
                .andExpect(jsonPath("$.firstName").value("Иван"));
    }
}