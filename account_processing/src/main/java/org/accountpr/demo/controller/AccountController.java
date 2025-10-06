package org.accountpr.demo.controller;

import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.t1hwork.starter.aop.annotations.HttpIncomeRequestLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @HttpIncomeRequestLog
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO dto) {
        AccountDTO created = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
  @HttpIncomeRequestLog
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
  @HttpIncomeRequestLog
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping("/client/{clientId}")
  @HttpIncomeRequestLog
    public ResponseEntity<List<AccountDTO>> getAccountsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(accountService.getAccountsByClientId(clientId));
    }

    @PutMapping("/{id}")
  @HttpIncomeRequestLog
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDTO dto
    ) {
        return ResponseEntity.ok(accountService.updateAccount(id, dto));
    }

    @DeleteMapping("/{id}")
  @HttpIncomeRequestLog
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
