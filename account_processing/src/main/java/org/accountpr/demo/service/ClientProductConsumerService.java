package org.accountpr.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.model.dto.CardDTO;
import org.accountpr.demo.model.dto.PaymentDTO;
import org.accountpr.demo.model.dto.TransactionDTO;
import org.accountpr.demo.model.enums.*;
import ru.t1hwork.starter.aop.annotations.LogDatasourceError;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProductConsumerService {
    private final AccountService accountService;
    private final CardService cardService;
    private final TransactionService transactionService;
    private final PaymentService paymentService;
    private final TransactionMonitoringService monitoringService;
    private final CreditPaymentService creditPaymentService;
    @KafkaListener(topics = "client_products", groupId = "account-service")
    @LogDatasourceError(type="ERROR")
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
   public void consumeClientTransaction(Map<String, Object> message){
        log.info("Received transaction message: {}", message); try {
            String transactionId = message.get("transactionId").toString();
            Long accountId = Long.valueOf(message.get("accountId").toString());
            Long cardId = message.get("cardId") != null ?
                    Long.valueOf(message.get("cardId").toString()) : null;
            TransactionType type = TransactionType.valueOf(message.get("type").toString());
            BigDecimal amount = new BigDecimal(message.get("amount").toString());

            // limit check
            if (cardId != null) {
                boolean allowed = monitoringService.checkTransactionLimit(cardId, type, amount);
                if (!allowed) {
                    log.warn("Transaction {} blocked due to limit exceeded", transactionId);
                    updateTransactionStatus(Long.valueOf(transactionId), TransactionStatus.CANCELLED);
                    return;
                }
            }

            processTransaction(transactionId, accountId, cardId, type, amount, message);

        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage(), e);
        }
    }
    @LogDatasourceError(type="ERROR")
    private void updateTransactionStatus(Long transactionId, TransactionStatus status) {
        try {
            TransactionDTO existingTransaction = transactionService.getTransactionById(transactionId);
            existingTransaction.setStatus(status);
            transactionService.updateTransaction(transactionId, existingTransaction);
            log.info("Transaction {} status updated to {}", transactionId, status);
        } catch (Exception e) {
            log.error("Error updating transaction {} status: {}", transactionId, e.getMessage());
        }
    }

    private void processTransaction(String transactionId, Long accountId, Long cardId,
                                    TransactionType type, BigDecimal amount, Map<String, Object> message) {
        AccountDTO account = accountService.getAccountById(accountId);

        // account status check
        if (isAccountBlocked(account)) {
            log.warn("Account {} is blocked, transaction rejected", accountId);
            return;
        }

        // process depending on the transaction type
        switch (type) {
            case DEPOSIT:
                handleDeposit(account, amount, message);
                break;
            case WITHDRAWAL:
                handleWithdrawal(account, amount, message);
                break;
            case PAYMENT:
                handlePayment(account, amount, message);
                break;
            case TRANSFER:
                handleTransfer(account, amount, message);
                break;
        }

        log.info("Transaction {} processed successfully", transactionId);
    }

    private boolean isAccountBlocked(AccountDTO account) {
        return AccountStatus.BLOCKED.name().equals(account.getStatus()) ||
                AccountStatus.ARRESTED.name().equals(account.getStatus());
    }

    private void handleDeposit(AccountDTO account, BigDecimal amount, Map<String, Object> message) {
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountService.updateAccount(account.getId(), account);

        // for credit account check payments
        if (Boolean.TRUE.equals(account.getIsRecalc())) {
            creditPaymentService.processCreditPayments(account);
        }

        log.info("Deposit processed for account {}: +{}", account.getId(), amount);
    }

    private void handleWithdrawal(AccountDTO account, BigDecimal amount, Map<String, Object> message) {
        if (account.getBalance().compareTo(amount) >= 0) {
            BigDecimal newBalance = account.getBalance().subtract(amount);
            account.setBalance(newBalance);
            accountService.updateAccount(account.getId(), account);
        } else {
            log.warn("Insufficient funds for withdrawal from account {}", account.getId());
        }
    }

    private void handlePayment(AccountDTO account, BigDecimal amount, Map<String, Object> message) {
        // right now this like withdrawal
        handleWithdrawal(account, amount, message);
    }

    private void handleTransfer(AccountDTO account, BigDecimal amount, Map<String, Object> message) {
        // right now this like withdrawal
        handleWithdrawal(account, amount, message);
    }

    private void checkCreditPayments(AccountDTO account) {
        if (Boolean.TRUE.equals(account.getIsRecalc())) {
            creditPaymentService.processCreditPayments(account);
        }
    }

    @KafkaListener(topics = "client_payments", groupId = "account-service")
    public void consumeClientPayment(Map<String, Object> message) {
        log.info("Received payment message: {}", message);

        try {
            String paymentId = message.get("paymentId").toString();
            Long accountId = Long.valueOf(message.get("accountId").toString());
            BigDecimal amount = new BigDecimal(message.get("amount").toString());
            String paymentType = message.get("type").toString();

            processPaymentMessage(paymentId, accountId, amount, paymentType, message);

        } catch (Exception e) {
            log.error("Error processing payment message: {}", e.getMessage(), e);
        }
    }

    private void processPaymentMessage(String paymentId, Long accountId, BigDecimal amount,
                                       String paymentType, Map<String, Object> message) {
        try {
            AccountDTO account = accountService.getAccountById(accountId);

            if (isAccountBlocked(account)) {
                log.warn("Account {} is blocked, payment rejected", accountId);
                return;
            }

            // payment type check
            if ("CREDIT_REPAYMENT".equals(paymentType)) {
                // loan payment
                creditPaymentService.processExternalCreditPayment(accountId, amount);
                log.info("Processed credit repayment for account {}: {}", accountId, amount);
            } else {
                // Regular payment - processed as a deposit
                handleDeposit(account, amount, message);
            }

            PaymentDTO paymentDTO = PaymentDTO.builder()
                    .accountId(accountId)
                    .paymentDate(LocalDate.now())
                    .amount(amount)
                    .isCredit("CREDIT_REPAYMENT".equals(paymentType))
                    .payedAt(LocalDateTime.now())
                    .expired(false)
                    .type(PaymentType.TRANSFER)
                    .build();

            paymentService.createPayment(paymentDTO);

            log.info("Payment {} processed successfully for account {}", paymentId, accountId);

        } catch (Exception e) {
            log.error("Error processing payment {}: {}", paymentId, e.getMessage());
        }
    }
    public void processCreditAccountCreation(Long accountId, BigDecimal amount,
                                             BigDecimal interestRate, Integer months) {
        try {
            creditPaymentService.createPaymentSchedule(accountId, amount, interestRate, months);
            log.info("Payment schedule created for credit account {}", accountId);
        } catch (Exception e) {
            log.error("Error creating payment schedule for account {}: {}", accountId, e.getMessage());
        }
    }
}
