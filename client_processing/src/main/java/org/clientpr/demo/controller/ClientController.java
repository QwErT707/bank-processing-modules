package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import org.clientpr.demo.model.dto.ClientDTO;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);
        return ResponseEntity.ok(createdClient);
    }

    @GetMapping
    @HttpIncomeRequestLog
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @HttpIncomeRequestLog
        public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/client-id/{clientId}")
    @HttpIncomeRequestLog
        public ResponseEntity<ClientDTO> getClientByClientId(@PathVariable String clientId) {
        ClientDTO client = clientService.getClientByClientId(clientId);
        return ResponseEntity.ok(client);
    }

    @GetMapping("/user/{userId}")
    @HttpIncomeRequestLog
        public ResponseEntity<List<ClientDTO>> getClientsByUserId(@PathVariable Long userId) {
        List<ClientDTO> clients = clientService.getClientsByUserId(userId);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/document-type/{documentType}")
    @HttpIncomeRequestLog
        public ResponseEntity<List<ClientDTO>> getClientsByDocumentType(@PathVariable DocumentType documentType) {
        List<ClientDTO> clients = clientService.getClientsByDocumentType(documentType);
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    @HttpIncomeRequestLog
        public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO updatedClient = clientService.updateClient(id, clientDTO);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/{id}")
    @HttpIncomeRequestLog
        public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}