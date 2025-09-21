package org.accountpr.demo.service;

import org.accountpr.demo.model.*;
import org.accountpr.demo.model.dto.PaymentDTO;
import org.accountpr.demo.model.enums.PaymentType;
import org.accountpr.demo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = Payment.hiddenBuilder()
                .accountId(dto.getAccountId())
                .paymentDate(dto.getPaymentDate())
                .amount(dto.getAmount())
                .isCredit(dto.getIsCredit())
                .payedAt(dto.getPayedAt())
                .type(dto.getType())
                .build();

        Payment saved = paymentRepository.save(payment);
        return convertToDTO(saved);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));
    }

    public List<PaymentDTO> getPaymentsByAccountId(Long accountId) {
        return paymentRepository.findByAccountId(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByType(PaymentType type) {
        return paymentRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByIsCredit(Boolean isCredit) {
        return paymentRepository.findByIsCredit(isCredit).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + id));

        payment.setAccountId(dto.getAccountId());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setAmount(dto.getAmount());
        payment.setIsCredit(dto.getIsCredit());
        payment.setPayedAt(dto.getPayedAt());
        payment.setType(dto.getType());

        Payment updated = paymentRepository.save(payment);
        return convertToDTO(updated);
    }

    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .accountId(payment.getAccountId())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .isCredit(payment.getIsCredit())
                .payedAt(payment.getPayedAt())
                .type(payment.getType())
                .build();
    }
}

