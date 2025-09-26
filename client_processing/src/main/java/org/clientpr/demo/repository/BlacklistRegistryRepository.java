package org.clientpr.demo.repository;

import org.clientpr.demo.model.BlacklistRegistry;
import org.clientpr.demo.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlacklistRegistryRepository extends JpaRepository<BlacklistRegistry, Long> {
    Optional<BlacklistRegistry> findByDocumentTypeAndDocumentId(DocumentType documentType, String documentId);

    List<BlacklistRegistry> findByDocumentType(DocumentType documentType);

    List<BlacklistRegistry> findByBlacklistExpirationDateAfter(LocalDateTime date);

    List<BlacklistRegistry> findByBlacklistExpirationDateBefore(LocalDateTime date);

    boolean existsByDocumentTypeAndDocumentId(DocumentType documentType, String documentId);
}
