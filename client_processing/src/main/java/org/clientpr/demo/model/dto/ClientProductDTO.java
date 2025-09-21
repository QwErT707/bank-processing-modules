package org.clientpr.demo.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clientpr.demo.model.enums.ProductStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientProductDTO {
    private Long id;

    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

    private LocalDateTime openDate;

    private LocalDateTime closeDate;

    @NotNull
    private ProductStatus status;
}
