package org.clientpr.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aop.annotations.HttpIncomeRequestLog;
import org.clientpr.demo.model.dto.BlacklistRegistryDTO;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.service.BlacklistRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlacklistRegistryController {
    private final BlacklistRegistryService blacklistRegistryService;
    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<BlacklistRegistryDTO> addToBlacklist(@Valid @RequestBody BlacklistRegistryDTO blacklistDTO) {
        BlacklistRegistryDTO saved = blacklistRegistryService.addToBlacklist(blacklistDTO);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromBlacklist(@PathVariable Long id) {
        blacklistRegistryService.removeFromBlacklist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isBlacklisted(
            @RequestParam DocumentType documentType,
            @RequestParam String documentId) {
        boolean isBlacklisted = blacklistRegistryService.isBlacklisted(documentType, documentId);
        return ResponseEntity.ok(isBlacklisted);
    }

    @GetMapping
    public ResponseEntity<List<BlacklistRegistryDTO>> getAllBlacklisted() {
        List<BlacklistRegistryDTO> blacklist = blacklistRegistryService.getAllBlacklisted();
        return ResponseEntity.ok(blacklist);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BlacklistRegistryDTO>> getActiveBlacklist() {
        List<BlacklistRegistryDTO> activeBlacklist = blacklistRegistryService.getActiveBlacklist();
        return ResponseEntity.ok(activeBlacklist);
    }

    @GetMapping("/expired")
    public ResponseEntity<List<BlacklistRegistryDTO>> getExpiredBlacklist() {
        List<BlacklistRegistryDTO> expiredBlacklist = blacklistRegistryService.getExpiredBlacklist();
        return ResponseEntity.ok(expiredBlacklist);
    }
}
