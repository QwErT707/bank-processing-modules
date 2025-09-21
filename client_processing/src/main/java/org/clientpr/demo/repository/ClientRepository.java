package org.clientpr.demo.repository;

import org.clientpr.demo.model.Client;
import org.clientpr.demo.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByClientId(String clientId);
    List<Client> findByUserId(Long userId);
    List<Client> findByDocumentType(DocumentType documentType);
    boolean existsByClientId(String clientId);
    boolean existsByDocumentId(String documentId);
}
