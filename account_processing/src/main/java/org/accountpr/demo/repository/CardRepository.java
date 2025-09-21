package org.accountpr.demo.repository;
import org.accountpr.demo.model.Card;
import org.accountpr.demo.model.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardId(String cardId);

    List<Card> findByAccountId(Long accountId);

    List<Card> findByStatus(CardStatus status);

    boolean existsByCardId(String cardId);
}

