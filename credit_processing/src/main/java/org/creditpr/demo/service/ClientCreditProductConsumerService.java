package org.creditpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.creditpr.demo.dto.PaymentRegistryDTO;
import org.creditpr.demo.dto.ProductRegistryDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCreditProductConsumerService {

    private final ProductRegistryService productRegistryService;
    private final PaymentRegistryService paymentRegistryService;
    private final RestTemplate restTemplate;

    @Value("${credit.limit:1000000}")
    private BigDecimal creditLimit;

    @Value("${client.service.url}")
    private String clientServiceUrl;

    @KafkaListener(topics = "client_credit_products", groupId = "credit-service")
    public void consumeClientCreditProductMessage(Map<String, Object> message) {
        log.info("Received client credit product message: {}", message);

        try {
            Long clientId = Long.valueOf(message.get("clientId").toString());
            Long productId = Long.valueOf(message.get("productId").toString());
            BigDecimal amount = new BigDecimal(message.get("amount").toString());
            BigDecimal interestRate = new BigDecimal(message.get("interestRate").toString());
            Integer monthCount = Integer.valueOf(message.get("monthCount").toString());
            Long accountId = Long.valueOf(message.get("accountId").toString());

            Map<String, Object> client = getClientFromMC1(clientId);
            if (client == null) {
                log.error("Client {} not found in MC-1", clientId);
                return;
            }
            String firstName=(String) client.get("firstName");
            String lastName=(String) client.get("lastName");

            log.info("Processing credit application for client: {} {}",firstName,lastName);
            boolean approved = checkCreditApproval(clientId, amount);

            if (!approved) {
                log.warn("Credit application rejected for client {}", clientId);
                return;
            }

            log.info("Credit application approved for client {}", clientId);
            ProductRegistryDTO productRegistryDTO = ProductRegistryDTO.builder()
                    .clientId(clientId)
                    .accountId(accountId)
                    .productId(productId)
                    .amount(amount)
                    .interestRate(interestRate)
                    .monthCount(monthCount)
                    .openDate(LocalDateTime.now())
                    .build();

            ProductRegistryDTO createdProduct = productRegistryService.createProductRegistry(productRegistryDTO);
            createPaymentSchedule(createdProduct, amount, interestRate, monthCount);

            log.info("Credit product created successfully: {}", createdProduct.getId());

        } catch (Exception e) {
            log.error("Error processing client credit product: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> getClientFromMC1(Long clientId) {
        try {
            String url = clientServiceUrl + "/api/clients/" + clientId;
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String,Object>>() {});
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Map<String, Object>)response.getBody();
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching client from MC-1: {}", e.getMessage());
            return null;
        }
    }

    private boolean checkCreditApproval(Long clientId, BigDecimal newAmount) {
        List<ProductRegistryDTO> existingProducts = productRegistryService.getProductRegistriesByClientId(clientId);
        BigDecimal totalDebt = existingProducts.stream()
                .map(ProductRegistryDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal proposedTotalDebt = totalDebt.add(newAmount);
        if (proposedTotalDebt.compareTo(creditLimit) > 0) {
            log.warn("Credit limit exceeded: {} > {}", proposedTotalDebt, creditLimit);
            return false;
        }
        if (!existingProducts.isEmpty()) {
            boolean hasDelays = productRegistryService.hasClientDelayedPayments(clientId);
            if (hasDelays) {
                log.warn("Client has past delays, credit rejected");
                return false;
            }
        }

        return true;
    }

    private void createPaymentSchedule(ProductRegistryDTO product, BigDecimal amount,
                                       BigDecimal annualRate, Integer months) {
        if (months <= 0 || annualRate.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid parameters for payment schedule: months={}, rate={}", months, annualRate);
            return;
        }
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal temp = BigDecimal.ONE.add(monthlyRate).pow(months);
        BigDecimal annuityPayment = amount.multiply(monthlyRate.multiply(temp))
                .divide(temp.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal remainingDebt = amount;
        LocalDateTime paymentDate = LocalDateTime.now().plusMonths(1);

        log.info("Creating payment schedule for product {}: amount={}, rate={}%, months={}, monthly payment={}",
                product.getId(), amount, annualRate, months, annuityPayment);

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = remainingDebt.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principal = annuityPayment.subtract(interest);
            if (i == months) {
                principal = remainingDebt;
                annuityPayment = principal.add(interest);
            }

            PaymentRegistryDTO payment = PaymentRegistryDTO.builder()
                    .productRegistryId(product.getId())
                    .paymentDate(paymentDate)
                    .amount(annuityPayment)
                    .interestRateAmount(interest)
                    .debtAmount(principal)
                    .expired(false)
                    .paymentExpirationDate(paymentDate.plusDays(30))
                    .build();

            paymentRegistryService.createPaymentRegistry(payment);
            remainingDebt = remainingDebt.subtract(principal);
            paymentDate = paymentDate.plusMonths(1);

            log.debug("Payment {}: principal={}, interest={}, remaining={}",
                    i, principal, interest, remainingDebt);
        }

        log.info("Payment schedule created with {} payments", months);
    }
}