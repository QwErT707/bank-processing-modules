package org.clientpr.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.clientpr.demo.model.enums.DocumentType;

import java.time.LocalDate;

@Entity
@Table(name="clients")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "hiddenBuilder")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, name = "client_id")
    private String clientId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Column(name = "document_prefix")
    private String documentPrefix;

    @Column(name = "document_suffix")
    private String documentSuffix;

    public static ClientBuilder builder(String clientId, Long userId, String firstName, String lastName,
                                        LocalDate dateOfBirth, DocumentType documentType, String documentId){
        return hiddenBuilder().clientId(clientId).userId(userId).firstName(firstName).lastName(lastName).dateOfBirth(dateOfBirth).documentType(documentType).documentId(documentId);
    }
}
