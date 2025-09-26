package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.clientpr.demo.model.dto.CardCreationRequestDTO;
import org.clientpr.demo.repository.ClientRepository;
import org.clientpr.demo.service.ClientService;
import org.clientpr.demo.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/card-requests")
@RequiredArgsConstructor
public class CardRequestController {
    private final KafkaProducerService kafkaProducerService;
    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @PostMapping("/client/{clientId}/cards/request")
    public ResponseEntity<String> requestCardCreation(
            @PathVariable Long clientId,
            @Valid @RequestBody CardCreationRequestDTO cardRequest) {

        if (!clientRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Client not found with id: " + clientId);
        }
        cardRequest.setRequestId(UUID.randomUUID().toString());
        cardRequest.setTimestamp(LocalDateTime.now());
        kafkaProducerService.sendCardCreationRequest(cardRequest);

        return ResponseEntity.ok("Card creation request sent for processing");
    }
}