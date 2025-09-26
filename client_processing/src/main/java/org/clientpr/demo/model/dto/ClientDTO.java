package org.clientpr.demo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clientpr.demo.model.enums.DocumentType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private Long id;
    @NotBlank
    @Pattern(regexp = "\\d{12}")
    private String clientId;
    @NotNull
    private Long userId;
    @NotBlank
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String middleName;
   @NotBlank
    @Size(max = 50)
    private String lastName;
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private DocumentType documentType;
    @NotBlank
    @Size(max = 20)
    private String documentId;
    @Size(max = 10)
    private String documentPrefix;
    @Size(max = 10)
    private String documentSuffix;    }

