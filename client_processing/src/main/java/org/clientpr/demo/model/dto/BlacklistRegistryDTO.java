package org.clientpr.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clientpr.demo.model.enums.DocumentType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistRegistryDTO {
    private Long id;

    @NotNull
    private DocumentType documentType;

    @NotBlank
    private String documentId;

    @NotNull
    private LocalDateTime blacklistedAt;

    private String reason;

    private LocalDateTime blacklistExpirationDate;
}
