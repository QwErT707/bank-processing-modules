package org.accountpr.demo.repository;


import org.accountpr.demo.model.CardTransactionMonitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CardTransactionMonitoringRepository extends JpaRepository<CardTransactionMonitoring, Long> {
    List<CardTransactionMonitoring> findByCardIdAndTransactionTimeAfterOrderByTransactionTimeDesc(
            Long cardId, LocalDateTime fromTime);
    @Query("SELECT COUNT(ctm) FROM CardTransactionMonitoring ctm WHERE ctm.cardId = :cardId AND ctm.transactionTime > :fromTime")
    long countTransactionsInTimeWindow(@Param("cardId") Long cardId, @Param("fromTime") LocalDateTime fromTime);

    @Modifying
    @Query("DELETE FROM CardTransactionMonitoring ctm WHERE ctm.transactionTime < :cleanupTime")
    int cleanupOldRecords(@Param("cleanupTime") LocalDateTime cleanupTime);

    @Query("SELECT DISTINCT ctm.cardId FROM CardTransactionMonitoring ctm WHERE ctm.transactionTime > :fromTime")
    List<Long> findActiveCardsInTimeWindow(@Param("fromTime") LocalDateTime fromTime);
}
