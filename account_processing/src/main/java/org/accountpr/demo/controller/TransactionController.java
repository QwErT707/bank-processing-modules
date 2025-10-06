package org.accountpr.demo.controller;

import org.accountpr.demo.model.dto.TransactionDTO;
import org.accountpr.demo.model.enums.TransactionStatus;
import org.accountpr.demo.model.enums.TransactionType;
import org.accountpr.demo.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO dto) {
        TransactionDTO createdTransaction = transactionService.createTransaction(dto);
        kafkaTemplate.send("client_transactions",
                createdTransaction.getId().toString(),
                convertToKafkaMessage(createdTransaction));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }
    private Map<String, Object> convertToKafkaMessage(TransactionDTO transaction) {
        Map<String, Object> message = new HashMap<>();
        message.put("transactionId", transaction.getId());
        message.put("accountId", transaction.getAccountId());
        message.put("cardId", transaction.getCardId());
        message.put("type", transaction.getType().name());
        message.put("amount", transaction.getAmount());
        message.put("timestamp", transaction.getTimestamp().toString());
        return message;
    }
    @GetMapping
    @HttpIncomeRequestLog
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/account/{accountId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
    }

    @GetMapping("/card/{cardId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCardId(@PathVariable Long cardId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCardId(cardId));
    }

    @GetMapping("/type/{type}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable TransactionType type) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(type));
    }

    @GetMapping("/status/{status}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<TransactionDTO>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }

    @PutMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<TransactionDTO> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO dto) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, dto));
    }

    @DeleteMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

