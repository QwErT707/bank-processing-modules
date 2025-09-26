package org.clientpr.demo.service;

import lombok.RequiredArgsConstructor;
import org.clientpr.demo.model.BlacklistRegistry;
import org.clientpr.demo.model.dto.BlacklistRegistryDTO;
import org.clientpr.demo.model.enums.DocumentType;
import org.clientpr.demo.repository.BlacklistRegistryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlacklistRegistryService {
    private final BlacklistRegistryRepository blacklistRegistryRepository;
    public BlacklistRegistryDTO addToBlacklist(BlacklistRegistryDTO blacklistDTO) {
                if (blacklistRegistryRepository.existsByDocumentTypeAndDocumentId(
                blacklistDTO.getDocumentType(), blacklistDTO.getDocumentId())) {
            throw new IllegalArgumentException("Document already blacklisted");
        }

        BlacklistRegistry blacklist = BlacklistRegistry.builder(
                blacklistDTO.getDocumentType(),
                        blacklistDTO.getDocumentId(),
                LocalDateTime.now(),
                blacklistDTO.getReason(),
                blacklistDTO.getBlacklistExpirationDate()
        ).build();

        BlacklistRegistry saved = blacklistRegistryRepository.save(blacklist);
        return convertToDTO(saved);
    }

    public void removeFromBlacklist(Long id) {
        blacklistRegistryRepository.deleteById(id);
    }

    public boolean isBlacklisted(DocumentType documentType, String documentId) {
        return blacklistRegistryRepository.existsByDocumentTypeAndDocumentId(documentType, documentId);
    }

    public List<BlacklistRegistryDTO> getAllBlacklisted() {
        return blacklistRegistryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BlacklistRegistryDTO> getActiveBlacklist() {
        return blacklistRegistryRepository.findByBlacklistExpirationDateAfter(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BlacklistRegistryDTO> getExpiredBlacklist() {
        return blacklistRegistryRepository.findByBlacklistExpirationDateBefore(LocalDateTime.now())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BlacklistRegistryDTO convertToDTO(BlacklistRegistry blacklist) {
        return BlacklistRegistryDTO.builder()
                .id(blacklist.getId())
                .documentType(blacklist.getDocumentType())
                .documentId(blacklist.getDocumentId())
                .blacklistedAt(blacklist.getBlacklistedAt())
                .reason(blacklist.getReason())
                .blacklistExpirationDate(blacklist.getBlacklistExpirationDate())
                .build();
    }
}
