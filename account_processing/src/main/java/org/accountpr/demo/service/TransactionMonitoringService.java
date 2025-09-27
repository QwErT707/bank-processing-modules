package org.accountpr.demo.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accountpr.demo.model.CardTransactionMonitoring;
import org.accountpr.demo.model.dto.AccountDTO;
import org.accountpr.demo.model.dto.CardDTO;
import org.accountpr.demo.model.enums.AccountStatus;
import org.accountpr.demo.model.enums.CardStatus;
import org.accountpr.demo.model.enums.TransactionType;
import org.accountpr.demo.repository.CardTransactionMonitoringRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionMonitoringService {
    private final AccountService accountService;
    private final CardService cardService;
    private final TransactionService transactionService;
    private final CardTransactionMonitoringRepository monitoringRepository;

    @Value("${transaction.limits.max-transactions-per-card:10}")
    private int maxTransactionsPerCard;

    @Value("${transaction.limits.time-window-minutes:5}")
    private int timeWindowMinutes;

    @Value("${transaction.limits.freeze-threshold:5}")
    private int freezeThreshold;

    @Value("${transaction.limits.cleanup-hours:24}")
    private int cleanupHours;

    @Transactional
    public boolean checkTransactionLimit(Long cardId, TransactionType transactionType, BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(timeWindowMinutes);

        // adding a transaction to the db
        CardTransactionMonitoring monitoringRecord = CardTransactionMonitoring.builder(cardId,now,transactionType.name(),amount)
                .build();

        monitoringRepository.save(monitoringRecord);

        //counting the nnumber of transactions in a time window
        long transactionCount = monitoringRepository.countTransactionsInTimeWindow(cardId, windowStart);

        log.debug("Card {} has {} transactions in last {} minutes",
                cardId, transactionCount, timeWindowMinutes);

        if (transactionCount >= maxTransactionsPerCard) {
            log.warn("Card {} exceeded transaction limit: {} transactions in {} minutes",
                    cardId, transactionCount, timeWindowMinutes);
            blockCardAndAccount(cardId);
            return false;
        }

        if (transactionCount >= freezeThreshold) {
            log.info("Card {} approaching limit: {} transactions in {} minutes",
                    cardId, transactionCount, timeWindowMinutes);
        }

        return true;
    }

    @Transactional
    public void blockCardAndAccount(Long cardId) {
        try {
            CardDTO card = cardService.getCardById(cardId);

            // update status card
            card.setStatus(CardStatus.BLOCKED.name());
            cardService.updateCard(cardId, card);

            // update status account
            AccountDTO account = accountService.getAccountById(card.getAccountId());
            account.setStatus(AccountStatus.BLOCKED.name());
            accountService.updateAccount(account.getId(), account);

            log.info("Blocked card {} and account {} due to transaction limit exceeded",
                    cardId, account.getId());

        } catch (Exception e) {
            log.error("Error blocking card and account for card {}: {}", cardId, e.getMessage());
        }
    }

    //Method for checking all cards for exceeding the limit (for scheduled tasks)
    @Transactional
    public void checkAllCardsForLimits() {
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(timeWindowMinutes);
        List<Long> activeCardIds = monitoringRepository.findActiveCardsInTimeWindow(windowStart);

        for (Long cardId : activeCardIds) {
            long transactionCount = monitoringRepository.countTransactionsInTimeWindow(cardId, windowStart);

            if (transactionCount >= maxTransactionsPerCard) {
                log.warn("Scheduled check: Card {} has {} transactions in last {} minutes - blocking",
                        cardId, transactionCount, timeWindowMinutes);
                blockCardAndAccount(cardId);
            }
        }
    }
    //Clearing old records (once per hour)
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupOldMonitoringRecords() {
        LocalDateTime cleanupTime = LocalDateTime.now().minusHours(cleanupHours);
        int deletedCount = monitoringRepository.cleanupOldRecords(cleanupTime);

        if (deletedCount > 0) {
            log.info("Cleaned up {} old monitoring records", deletedCount);
        }
    }

//getting statistic for on the card
public TransactionStats getCardTransactionStats(Long cardId, Integer minutes) {
        LocalDateTime fromTime = LocalDateTime.now().minusMinutes(minutes != null ? minutes : timeWindowMinutes);

        long transactionCount = monitoringRepository.countTransactionsInTimeWindow(cardId, fromTime);
        List<CardTransactionMonitoring> recentTransactions =
                monitoringRepository.findByCardIdAndTransactionTimeAfterOrderByTransactionTimeDesc(cardId, fromTime);

        BigDecimal totalAmount = recentTransactions.stream()
                .map(CardTransactionMonitoring::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TransactionStats.builder()
                .cardId(cardId)
                .transactionCount(transactionCount)
                .totalAmount(totalAmount)
                .timeWindowMinutes(minutes != null ? minutes : timeWindowMinutes)
                .recentTransactions(recentTransactions)
                .build();
    }

    // DTO for statistic
    @Data
    @Builder
    public static class TransactionStats {
        private Long cardId;
        private long transactionCount;
        private BigDecimal totalAmount;
        private int timeWindowMinutes;
        private List<CardTransactionMonitoring> recentTransactions;
    }
}
