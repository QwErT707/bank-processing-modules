package org.accountpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accountpr.demo.model.Account;
import org.accountpr.demo.model.Card;
import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.model.dto.CardDTO;
import org.accountpr.demo.model.enums.AccountStatus;
import org.accountpr.demo.model.enums.CardStatus;
import org.accountpr.demo.model.enums.PaymentSystem;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProductConsumerService {
    private final AccountService accountService;
    private final CardService cardService;
    @KafkaListener(topics = "client_products", groupId = "account-service")
    public void consumeClientProductMessage(Map<String, Object> message){
        log.info("Received client product message: {}", message);
        try {
            if ("CREATE".equals(message.get("operationType"))) {
            AccountDTO accountDTO = parseToAccountDTO(message);

            AccountDTO createdAccount = accountService.createAccount(accountDTO);
            log.info("Account created successfully: {}", createdAccount.getId());}
            else { log.info("Skipping non-CREATE operation: {}", message.get("operationType"));
                }
        } catch (Exception e) {
            log.error("Error processing client product: {}", e.getMessage());
        }
    }
    private AccountDTO parseToAccountDTO(Map<String, Object> message) {
        return AccountDTO.builder()
                .clientId(Long.valueOf(message.get("clientId").toString()))
        .productId(Long.valueOf(message.get("productId").toString()))
        .balance(BigDecimal.ZERO)
        .interestRate(BigDecimal.ZERO)
        .isRecalc(false)
        .cardExist(false)
        .status(AccountStatus.ACTIVE.name())
                .build();
    }
    @KafkaListener(topics = "client_cards", groupId = "account-service")
    public void consumeCardCreationRequest(Map<String, Object> message) {
        log.info("Received card creation request: {}", message);

        try { AccountDTO account = accountService.getAccountById(Long.valueOf(message.get("accountId").toString()));
            if (account!=null && !account.getStatus().equals("BLOCKED")){
                CardDTO cardDTO=parseToCardDTO(message);
                CardDTO createdCard = cardService.createCard(cardDTO);
                log.info("Card created successfully: {}", createdCard.getId());
            } else {
                log.warn("Account {} is blocked. Card creation rejected.", message.get("accountId"));
            }
        } catch (Exception e) {
            log.error("Error processing card creation request: {}", e.getMessage());
        }
    }
    private CardDTO parseToCardDTO(Map<String, Object> message) {
        return CardDTO.builder()
                .accountId(Long.valueOf(message.get("accountId").toString()))
                .cardId(message.get("requestId").toString())
                .paymentSystem(PaymentSystem.valueOf(message.get("cardType").toString()))
                .status(CardStatus.ACTIVE.name())
                .build();
    }

    @KafkaListener(topics = "client_transactions", groupId = "account-service")
   public void consumeClientTransaction(){
        log.info("It's empty here now");
    }
}
