package org.creditpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.creditpr.demo.dto.PaymentRegistryDTO;
import org.creditpr.demo.model.PaymentRegistry;
import org.creditpr.demo.repository.PaymentRegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentRegistryService {
    private final PaymentRegistryRepository repository;

    public PaymentRegistryDTO createPaymentRegistry(PaymentRegistryDTO dto){
        PaymentRegistry paymentRegistry=PaymentRegistry.hiddenBuilder()
                .productRegistryId(dto.getProductRegistryId())
                .paymentDate(dto.getPaymentDate())
                .amount(dto.getAmount())
                .interestRateAmount(dto.getInterestRateAmount())
                .debtAmount(dto.getDebtAmount())
                .expired(dto.getExpired())
                .paymentExpirationDate(dto.getPaymentExpirationDate())
                .build();
        PaymentRegistry saved=repository.save(paymentRegistry);
        return convertToDTO(saved);
    }
    public Optional<PaymentRegistryDTO> getPaymentRegistryById(Long id) {
        return repository.findById(id).map(this::convertToDTO);
    }

    public List<PaymentRegistryDTO> getAllPaymentRegistries() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentRegistryDTO> getPaymentRegistriesByProductRegistryId(Long productRegistryId) {
        return repository.findByProductRegistryId(productRegistryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentRegistryDTO> getExpiredPayments() {
        return repository.findByExpired(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentRegistryDTO updatePaymentRegistry(Long id, PaymentRegistryDTO dto) {
        PaymentRegistry existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PaymentRegistry not found with id: " + id));

        existing.setProductRegistryId(dto.getProductRegistryId());
        existing.setPaymentDate(dto.getPaymentDate());
        existing.setAmount(dto.getAmount());
        existing.setInterestRateAmount(dto.getInterestRateAmount());
        existing.setDebtAmount(dto.getDebtAmount());
        existing.setExpired(dto.getExpired());
        existing.setPaymentExpirationDate(dto.getPaymentExpirationDate());

        PaymentRegistry updated = repository.save(existing);
        return convertToDTO(updated);
    }

    public PaymentRegistryDTO updatePaymentExpiredStatus(Long id, Boolean expired) {
        PaymentRegistry existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PaymentRegistry not found with id: " + id));

        existing.setExpired(expired);

        PaymentRegistry updated = repository.save(existing);
        return convertToDTO(updated);
    }

    public void deletePaymentRegistry(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("PaymentRegistry not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private PaymentRegistryDTO convertToDTO(PaymentRegistry paymentRegistry) {
        return PaymentRegistryDTO.builder()
                .id(paymentRegistry.getId())
                .productRegistryId(paymentRegistry.getProductRegistryId())
                .paymentDate(paymentRegistry.getPaymentDate())
                .amount(paymentRegistry.getAmount())
                .interestRateAmount(paymentRegistry.getInterestRateAmount())
                .debtAmount(paymentRegistry.getDebtAmount())
                .expired(paymentRegistry.getExpired())
                .paymentExpirationDate(paymentRegistry.getPaymentExpirationDate())
                .build();
    }
}
