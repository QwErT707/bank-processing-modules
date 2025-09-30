package org.accountpr.demo.controller;

import org.accountpr.demo.model.dto.PaymentDTO;
import org.accountpr.demo.model.enums.PaymentType;
import org.accountpr.demo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aop.annotations.HttpIncomeRequestLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(dto));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/account/{accountId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentDTO>> getPaymentsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(paymentService.getPaymentsByAccountId(accountId));
    }

    @GetMapping("/type/{type}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentDTO>> getPaymentsByType(@PathVariable PaymentType type) {
        return ResponseEntity.ok(paymentService.getPaymentsByType(type));
    }

    @GetMapping("/credit/{isCredit}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<PaymentDTO>> getPaymentsByIsCredit(@PathVariable Boolean isCredit) {
        return ResponseEntity.ok(paymentService.getPaymentsByIsCredit(isCredit));
    }

    @PutMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    @DeleteMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
