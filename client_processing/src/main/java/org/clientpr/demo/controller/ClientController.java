package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clientpr.demo.service.RoleService;
import org.springframework.http.HttpStatus;
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
    private final RoleService roleService;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        ClientDTO createdClient = clientService.createClient(clientDTO);
        return ResponseEntity.ok(createdClient);
    }
    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockClient(@PathVariable Long id,
                                         @RequestHeader("X-User-Id") Long userId,
                                         @RequestParam(required = false) String reason) {
        if (!roleService.hasGrandEmployeeOrMasterRole(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with MASTER or GRAND_EMPLOYEE role can block clients");
        }

        try {
            ClientDTO blockedClient = clientService.blockClient(id, reason);
            return ResponseEntity.ok(blockedClient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblockClient(@PathVariable Long id,
                                           @RequestHeader("X-User-Id") Long userId) {
        if (!roleService.hasGrandEmployeeOrMasterRole(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only users with MASTER or GRAND_EMPLOYEE role can unblock clients");
        }        try {
            ClientDTO unblockedClient = clientService.unblockClient(id);
            return ResponseEntity.ok(unblockedClient);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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