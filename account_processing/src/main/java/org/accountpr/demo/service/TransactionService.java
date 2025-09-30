package org.accountpr.demo.service;

import org.accountpr.demo.model.*;
import org.accountpr.demo.model.enums.TransactionStatus;
import org.accountpr.demo.model.enums.TransactionType;
import org.accountpr.demo.repository.TransactionRepository;
import org.accountpr.demo.model.dto.TransactionDTO;
import org.aop.annotations.LogDatasourceError;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @LogDatasourceError(type = "ERROR")
    public TransactionDTO createTransaction(TransactionDTO dto) {
        Transaction transaction = Transaction.builder(
                dto.getAccountId(),
                dto.getCardId(),
                dto.getType(),
                dto.getAmount(),
                dto.getStatus(),
                dto.getTimestamp()
        ).build();

        Transaction saved = transactionRepository.save(transaction);
        return convertToDTO(saved);
    }

    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));
    }

    public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByCardId(Long cardId) {
        return transactionRepository.findByCardId(cardId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @LogDatasourceError(type = "ERROR")
    public TransactionDTO updateTransaction(Long id, TransactionDTO dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with id: " + id));

        transaction.setAccountId(dto.getAccountId());
        transaction.setCardId(dto.getCardId());
        transaction.setType(dto.getType());
        transaction.setAmount(dto.getAmount());
        transaction.setStatus(dto.getStatus());
        transaction.setTimestamp(dto.getTimestamp());

        Transaction updated = transactionRepository.save(transaction);
        return convertToDTO(updated);
    }

    @LogDatasourceError(type = "ERROR")
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new IllegalArgumentException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .cardId(transaction.getCardId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
