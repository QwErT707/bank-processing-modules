package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aop.annotations.HttpIncomeRequestLog;
import org.clientpr.demo.model.dto.CardCreationRequestDTO;
import org.clientpr.demo.model.dto.ClientProductDTO;
import org.clientpr.demo.model.enums.ProductStatus;
import org.clientpr.demo.repository.ClientRepository;
import org.clientpr.demo.service.ClientProductService;
import org.clientpr.demo.service.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/client-products")
@RequiredArgsConstructor
public class ClientProductController {

    private final ClientProductService clientProductService;
    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<ClientProductDTO> createClientProduct(@Valid @RequestBody ClientProductDTO clientProductDTO) {
        ClientProductDTO createdClientProduct = clientProductService.createClientProduct(clientProductDTO);
        return ResponseEntity.ok(createdClientProduct);
    }

    @GetMapping
    public ResponseEntity<List<ClientProductDTO>> getAllClientProducts() {
        List<ClientProductDTO> clientProducts = clientProductService.getAllClientProducts();
        return ResponseEntity.ok(clientProducts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientProductDTO> getClientProductById(@PathVariable Long id) {
        ClientProductDTO clientProduct = clientProductService.getClientProductById(id);
        return ResponseEntity.ok(clientProduct);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClientProductDTO>> getClientProductsByClientId(@PathVariable Long clientId) {
        List<ClientProductDTO> clientProducts = clientProductService.getClientProductsByClientId(clientId);
        return ResponseEntity.ok(clientProducts);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ClientProductDTO>> getClientProductsByProductId(@PathVariable Long productId) {
        List<ClientProductDTO> clientProducts = clientProductService.getClientProductsByProductId(productId);
        return ResponseEntity.ok(clientProducts);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClientProductDTO>> getClientProductsByStatus(@PathVariable ProductStatus status) {
        List<ClientProductDTO> clientProducts = clientProductService.getClientProductsByStatus(status);
        return ResponseEntity.ok(clientProducts);
    }

    @GetMapping("/client/{clientId}/status/{status}")
    public ResponseEntity<List<ClientProductDTO>> getClientProductsByClientIdAndStatus(
            @PathVariable Long clientId, @PathVariable ProductStatus status) {
        List<ClientProductDTO> clientProducts = clientProductService.getClientProductsByClientIdAndStatus(clientId, status);
        return ResponseEntity.ok(clientProducts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientProductDTO> updateClientProduct(@PathVariable Long id, @Valid @RequestBody ClientProductDTO clientProductDTO) {
        ClientProductDTO updatedClientProduct = clientProductService.updateClientProduct(id, clientProductDTO);
        return ResponseEntity.ok(updatedClientProduct);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClientProductDTO> updateClientProductStatus(@PathVariable Long id, @RequestParam ProductStatus status) {
        ClientProductDTO updatedClientProduct = clientProductService.updateClientProductStatus(id, status);
        return ResponseEntity.ok(updatedClientProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClientProduct(@PathVariable Long id) {
        clientProductService.deleteClientProduct(id);
        return ResponseEntity.noContent().build();
    }
}
