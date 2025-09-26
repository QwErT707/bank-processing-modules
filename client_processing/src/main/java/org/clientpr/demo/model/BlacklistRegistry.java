package org.clientpr.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.clientpr.demo.model.enums.DocumentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklist_registry")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class BlacklistRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    @Column(name = "reason")
    private String reason;

    @Column(name = "blacklist_expiration_date")
    private LocalDateTime blacklistExpirationDate;

    public static BlacklistRegistryBuilder builder(DocumentType documentType, String documentId, LocalDateTime blacklistedAt, String reason, LocalDateTime blacklistExpirationDate){
return hiddenBuilder().documentType(documentType).documentId(documentId).blacklistedAt(blacklistedAt).reason(reason).blacklistExpirationDate(blacklistExpirationDate);
    }
}
