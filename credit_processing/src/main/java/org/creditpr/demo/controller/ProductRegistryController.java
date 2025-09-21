package org.creditpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.creditpr.demo.dto.ProductRegistryDTO;
import org.creditpr.demo.service.ProductRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-registry")
@RequiredArgsConstructor
public class ProductRegistryController {
    private final ProductRegistryService service;

    @PostMapping
    public ResponseEntity<ProductRegistryDTO> createProductRegistry(
            @Valid @RequestBody ProductRegistryDTO dto) {
        ProductRegistryDTO created = service.createProductRegistry(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRegistryDTO> getProductRegistryById(@PathVariable Long id) {
        return service.getProductRegistryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductRegistryDTO>> getAllProductRegistries() {
        return ResponseEntity.ok(service.getAllProductRegistries());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ProductRegistryDTO>> getProductRegistriesByClientId(
            @PathVariable Long clientId) {
        return ResponseEntity.ok(service.getProductRegistriesByClientId(clientId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductRegistryDTO>> getProductRegistriesByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(service.getProductRegistriesByProductId(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductRegistryDTO> updateProductRegistry(
            @PathVariable Long id,
            @Valid @RequestBody ProductRegistryDTO dto) {
        try {
            ProductRegistryDTO result = service.updateProductRegistry(id, dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductRegistry(@PathVariable Long id) {
        try {
            service.deleteProductRegistry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
