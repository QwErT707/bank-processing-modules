package org.clientpr.demo.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clientpr.demo.model.enums.ProductKey;

import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ProductDTO {
        private Long id;
        @NotBlank
        private String name;
        @NotNull
        private ProductKey key;
        private LocalDateTime createDate;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private String productId;

    }
