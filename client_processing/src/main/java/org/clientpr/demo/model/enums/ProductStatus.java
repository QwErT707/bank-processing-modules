package org.clientpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    BLOCKED("BLOCKED"),
    ARRESTED("ARRESTED");
    private final String value;
    ProductStatus(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }
}
