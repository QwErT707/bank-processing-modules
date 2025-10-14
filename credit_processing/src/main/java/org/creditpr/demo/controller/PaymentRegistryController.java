package org.creditpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import org.creditpr.demo.dto.PaymentRegistryDTO;
import org.creditpr.demo.service.PaymentRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-registry")
@RequiredArgsConstructor
public class PaymentRegistryController {
    private final PaymentRegistryService service;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentRegistryDTO> createPaymentRegistry(
            @Valid @RequestBody PaymentRegistryDTO dto) {
        PaymentRegistryDTO created = service.createPaymentRegistry(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentRegistryDTO> getPaymentRegistryById(@PathVariable Long id) {
        return service.getPaymentRegistryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentRegistryDTO>> getAllPaymentRegistries() {
        return ResponseEntity.ok(service.getAllPaymentRegistries());
    }

    @GetMapping("/product-registry/{productRegistryId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentRegistryDTO>> getPaymentsByProductRegistryId(
            @PathVariable Long productRegistryId) {
        return ResponseEntity.ok(service.getPaymentRegistriesByProductRegistryId(productRegistryId));
    }

    @GetMapping("/expired")
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentRegistryDTO>> getExpiredPayments() {
        return ResponseEntity.ok(service.getExpiredPayments());
    }

    @PutMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentRegistryDTO> updatePaymentRegistry(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRegistryDTO dto) {
        try {
            PaymentRegistryDTO result = service.updatePaymentRegistry(id, dto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/expired")
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentRegistryDTO> updatePaymentExpiredStatus(
            @PathVariable Long id,
            @RequestParam Boolean expired) {
        try {
            PaymentRegistryDTO result = service.updatePaymentExpiredStatus(id, expired);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<Void> deletePaymentRegistry(@PathVariable Long id) {
        try {
            service.deletePaymentRegistry(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
