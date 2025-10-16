package org.clientpr.demo.service;

import org.clientpr.demo.model.Client;
import org.clientpr.demo.model.dto.ClientDTO;
import org.clientpr.demo.model.enums.ClientRole;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.repository.BlacklistRegistryRepository;
import org.clientpr.demo.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BlacklistRegistryRepository blacklistRegistryRepository;
    @Mock
    private BlacklistRegistryService blacklistRegistryService;
    @InjectMocks
    private ClientService clientService;

    private ClientDTO validClientDTO;
    private Client validClient;

    @BeforeEach
    void setUp(){
        validClientDTO=ClientDTO.builder()
                .clientId("CLIENT123")
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .documentType(DocumentType.PASSPORT)
                .documentId("1234567890").build();

        validClient=Client.builder(
                "CLIENT123", 1L, "John", "Ytr", "Doe",
                LocalDate.of(1990, 1, 1), DocumentType.PASSPORT,
                "1234567890", null, null
        ).build();
        validClient.setId(1L);validClient.setClientRole(ClientRole.CURRENT_CLIENT.toString());
    }

    @Test
    void createClient_WithValidData_ShouldCreateClient(){
        when(clientRepository.existsByClientId("CLIENT123")).thenReturn(false);
        when(clientRepository.existsByDocumentId("1234567890")).thenReturn(false);
        when(blacklistRegistryRepository.existsByDocumentTypeAndDocumentId(DocumentType.PASSPORT,"1234567890")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(validClient);
   ClientDTO result=clientService.createClient(validClientDTO);
   assertNotNull(result);
   assertEquals("CLIENT123", result.getClientId());
   //assertEquals(ClientRole.CURRENT_CLIENT.toString(), result.getClientRole());
verify(clientRepository).save(any(Client.class));
    }
    @Test
    void createClientWithExistingClientId_ShouldThrowException(){
        when(clientRepository.existsByClientId("CLIENT123")).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                ()->clientService.createClient(validClientDTO));
        assertEquals("Client with this clientId already exists: CLIENT123", exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }
    @Test
    void createClient_WithExistingDocumentId_ShouldThrowException(){
        when(clientRepository.existsByClientId("CLIENT123")).thenReturn(false);
        when(clientRepository.existsByDocumentId("1234567890")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clientService.createClient(validClientDTO));

        assertEquals("Client with this documentId already exists: 1234567890", exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }
    @Test
    void createClient_WithClientInBlacklist_ShouldThrowException() {
        when(clientRepository.existsByClientId("CLIENT123")).thenReturn(false);
        when(clientRepository.existsByDocumentId("1234567890")).thenReturn(false);
        when(blacklistRegistryRepository.existsByDocumentTypeAndDocumentId(DocumentType.PASSPORT, "1234567890")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clientService.createClient(validClientDTO));

        assertEquals("Client in blacklist: CLIENT123", exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void getClientById_WithExistingId_ShouldReturnClient() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));

        ClientDTO result = clientService.getClientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("CLIENT123", result.getClientId());
    }

    @Test
    void getClientById_WithNonExistingId_ShouldThrowException() {
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> clientService.getClientById(999L));

        assertEquals("Client not found with id: 999", exception.getMessage());
    }

    @Test
    void blockClient_ShouldChangeRoleAndAddToBlacklist() {
        Client blockedClient = Client.builder(
                "CLIENT123", 1L, "John", null, "Doe",
                LocalDate.of(1990, 1, 1), DocumentType.PASSPORT,
                "1234567890", null, null
        ).build();
        blockedClient.setId(1L);
        blockedClient.setClientRole(ClientRole.BLOCKED_CLIENT.toString());
        when(clientRepository.findById(1L)).thenReturn(Optional.of(validClient));
        when(clientRepository.save(any(Client.class))).thenReturn(validClient);

        ClientDTO result = clientService.blockClient(1L, "Fraud detected");
        assertNotNull(result);
     //   assertEquals(ClientRole.BLOCKED_CLIENT.toString(), result.getClientRole());
        verify(blacklistRegistryService).addToBlacklist(any());
    }
}
