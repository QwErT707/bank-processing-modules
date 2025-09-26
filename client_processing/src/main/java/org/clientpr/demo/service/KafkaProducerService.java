package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clientpr.demo.model.ClientProduct;
import org.clientpr.demo.model.Product;
import org.clientpr.demo.model.dto.CardCreationRequestDTO;
import org.clientpr.demo.model.enums.ProductKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductService productService;

    // crutch
    @Value("${credit.default.interest-rate:15.0}")
    private BigDecimal defaultInterestRate;

    @Value("${credit.default.months:24}")
    private Integer defaultMonths;

    @Value("${credit.default.amount:100000}")
    private BigDecimal defaultAmount;

    public void sendToClientProducts(ClientProduct clientProduct, String operationType){
        ProductKey productKey=productService.getProductById(clientProduct.getProductId()).getKey();
        Map<String, Object> message= new HashMap<>();
        message.put("operationType", operationType);
        message.put("clientProductId", clientProduct.getId());
        message.put("clientId", clientProduct.getClientId());
        message.put("productId", clientProduct.getProductId());
        message.put("openDate", clientProduct.getOpenDate() != null ?
                clientProduct.getOpenDate().toString() : null);
        message.put("closeDate", clientProduct.getCloseDate() != null ?
                clientProduct.getCloseDate().toString() : null);
        message.put("timestamp", System.currentTimeMillis());
String partKey=clientProduct.getClientId().toString();
        if (isCreditProduct(productKey)) {
            addCreditFields(message, productKey);
            kafkaTemplate.send("client_credit_products",partKey, message);
            log.info("Sent credit product message to client_credit_products: {}", message);

        }
        else if(isDepositOrCurrentProduct(productKey)){ kafkaTemplate.send("client_products",partKey, message);}
        else if(isCardProduct(productKey)){kafkaTemplate.send("client_cards",partKey, message);}
    }
    private void addCreditFields(Map<String, Object> message, ProductKey productKey) {
        BigDecimal interestRate = getInterestRateForProduct(productKey);
        Integer months = getMonthsForProduct(productKey);
        BigDecimal amount = getAmountForProduct(productKey);

        message.put("interestRate", interestRate);
        message.put("monthCount", months);
        message.put("amount", amount);
        message.put("accountId", generateAccountId());
    }

    private BigDecimal getInterestRateForProduct(ProductKey productKey) {
        return switch (productKey) {
            case IPO -> new BigDecimal("12.5");  // mortgage - lower rate
            case PC -> new BigDecimal("15.0");   // cons loan
            case AC -> new BigDecimal("18.0");   // car loan
            default -> defaultInterestRate;
        };
    }

    private Integer getMonthsForProduct(ProductKey productKey) {
        return switch (productKey) {
            case IPO -> 240;  // morTgage - 20 years
            case PC -> 36;    // consumer - 3 years
            case AC -> 60;    // car Loan  - 5 years
            default -> defaultMonths;
        };
    }

    private BigDecimal getAmountForProduct(ProductKey productKey) {
        return switch (productKey) {
            case IPO -> new BigDecimal("5000000.00");  // morTgage - 5 million
            case PC -> new BigDecimal("300000.00");    // consumer - 300k
            case AC -> new BigDecimal("1500000.00");   // car Loan - 1.5 million
            default -> defaultAmount;
        };
    }

    private Long generateAccountId() {
        // A temporary accountId is generated; in a real project it should come from ms-2, but then it turns out to be too much(
        return System.currentTimeMillis() % 1000000L;
    }

     private boolean isCreditProduct(ProductKey productKey) {
        return productKey == ProductKey.IPO ||
                productKey == ProductKey.PC ||
                productKey == ProductKey.AC;
    } private boolean isCardProduct(ProductKey productKey) {
        return productKey == ProductKey.BS ||
                productKey == ProductKey.INS;
    }
    private boolean isDepositOrCurrentProduct(ProductKey productKey) {
        return productKey == ProductKey.DC ||
                productKey == ProductKey.CC ||
                productKey == ProductKey.NS ||
                productKey == ProductKey.PENS;
    }


    public void sendCardCreationRequest(CardCreationRequestDTO cardRequest) {
        String key = cardRequest.getRequestId();

        Map<String, Object> message=new HashMap<>();
        message.put("requestId", cardRequest.getRequestId());
        message.put("accountId", cardRequest.getAccountId());
        message.put("cardType", cardRequest.getCardType());
        message.put("currency", cardRequest.getCurrency());
        //message.put("closeDate",cardRequest.getTimestamp());
                message.put("timestamp", System.currentTimeMillis());
        kafkaTemplate.send("client_cards", key, message);
        log.info("Sent card creation request to client_cards topic: {}", cardRequest);
    }
}

