package org.accountpr.demo.service;

import org.accountpr.demo.model.*;
import org.accountpr.demo.model.dto.CardDTO;
import org.accountpr.demo.model.enums.CardStatus;
import org.accountpr.demo.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {

    private final CardRepository cardRepository;

    public CardDTO createCard(CardDTO dto) {
        if (cardRepository.existsByCardId(dto.getCardId())) {
            throw new IllegalArgumentException("Card with this cardId already exists: " + dto.getCardId());
        }

        Card card = Card.hiddenBuilder()
                .accountId(dto.getAccountId())
                .cardId(dto.getCardId())
                .paymentSystem(dto.getPaymentSystem())
                .status(CardStatus.valueOf(dto.getStatus()))
                .build();

        Card saved = cardRepository.save(card);
        return convertToDTO(saved);
    }

    public List<CardDTO> getAllCards() {
        return cardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CardDTO getCardById(Long id) {
        return cardRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));
    }

    public CardDTO getCardByCardId(String cardId) {
        return cardRepository.findByCardId(cardId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with cardId: " + cardId));
    }

    public List<CardDTO> getCardsByAccountId(Long accountId) {
        return cardRepository.findByAccountId(accountId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CardDTO updateCard(Long id, CardDTO dto) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));

        if (!card.getCardId().equals(dto.getCardId()) &&
                cardRepository.existsByCardId(dto.getCardId())) {
            throw new IllegalArgumentException("Card with this cardId already exists: " + dto.getCardId());
        }

        card.setAccountId(dto.getAccountId());
        card.setCardId(dto.getCardId());
        card.setPaymentSystem(dto.getPaymentSystem());
        card.setStatus(CardStatus.valueOf(dto.getStatus()));

        Card updated = cardRepository.save(card);
        return convertToDTO(updated);
    }

    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new IllegalArgumentException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    private CardDTO convertToDTO(Card card) {
        return CardDTO.builder()
                .id(card.getId())
                .accountId(card.getAccountId())
                .cardId(card.getCardId())
                .paymentSystem(card.getPaymentSystem())
                .status(card.getStatus().name())
                .build();
    }
}

