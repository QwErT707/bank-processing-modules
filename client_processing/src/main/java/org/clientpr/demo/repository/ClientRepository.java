package org.clientpr.demo.repository;

import org.aop.annotations.Cached;
import org.clientpr.demo.model.Client;
import org.clientpr.demo.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Cached(cacheName = "clients.byClientId", ttl = 600000)
    Optional<Client> findByClientId(String clientId);
    @Cached
    List<Client> findByUserId(Long userId);
    @Cached(cacheName = "clients.byDocumentType")
    List<Client> findByDocumentType(DocumentType documentType);
    boolean existsByClientId(String clientId);
    boolean existsByDocumentId(String documentId);
}
