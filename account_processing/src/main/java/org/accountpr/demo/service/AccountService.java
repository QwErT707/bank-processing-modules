package org.accountpr.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.accountpr.demo.model.Account;
import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.model.enums.AccountStatus;
import org.accountpr.demo.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountDTO createAccount(AccountDTO dto) {
        Account account = Account.hiddenBuilder()
                .clientId(dto.getClientId())
                .productId(dto.getProductId())
                .balance(dto.getBalance())
                .interestRate(dto.getInterestRate())
                .isRecalc(dto.getIsRecalc())
                .cardExist(dto.getCardExist())
                .status(AccountStatus.valueOf(dto.getStatus()))
                .build();

        Account saved = accountRepository.save(account);
        return convertToDTO(saved);
    }

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO getAccountById(Long id) {
        return accountRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));
    }

    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + id));

        existing.setClientId(dto.getClientId());
        existing.setProductId(dto.getProductId());
        existing.setBalance(dto.getBalance());
        existing.setInterestRate(dto.getInterestRate());
        existing.setIsRecalc(dto.getIsRecalc());
        existing.setCardExist(dto.getCardExist());
        existing.setStatus(AccountStatus.valueOf(dto.getStatus()));

        Account updated = accountRepository.save(existing);
        return convertToDTO(updated);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    private AccountDTO convertToDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .clientId(account.getClientId())
                .productId(account.getProductId())
                .balance(account.getBalance())
                .interestRate(account.getInterestRate())
                .isRecalc(account.getIsRecalc())
                .cardExist(account.getCardExist())
                .status(account.getStatus().name())
                .build();
    }
}

