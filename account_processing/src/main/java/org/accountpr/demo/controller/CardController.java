package org.accountpr.demo.controller;

import org.accountpr.demo.model.dto.CardDTO;
import org.accountpr.demo.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aop.annotations.HttpIncomeRequestLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<CardDTO> createCard(@Valid @RequestBody CardDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.createCard(dto));
    }

    @GetMapping
    @HttpIncomeRequestLog
    public ResponseEntity<List<CardDTO>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    @GetMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<CardDTO> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @GetMapping("/cardId/{cardId}")
    @HttpIncomeRequestLog
    public ResponseEntity<CardDTO> getCardByCardId(@PathVariable String cardId) {
        return ResponseEntity.ok(cardService.getCardByCardId(cardId));
    }

    @GetMapping("/account/{accountId}")
    @HttpIncomeRequestLog
    public ResponseEntity<List<CardDTO>> getCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.getCardsByAccountId(accountId));
    }

    @PutMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<CardDTO> updateCard(@PathVariable Long id, @Valid @RequestBody CardDTO dto) {
        return ResponseEntity.ok(cardService.updateCard(id, dto));
    }

    @DeleteMapping("/{id}")
    @HttpIncomeRequestLog
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
