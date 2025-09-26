package org.accountpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    ALLOWED("ALLOWED"),
    PROCESSING("PROCESSING"),
    COMPLETE("COMPLETE"),
    CANCELLED("CANCELLED");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
