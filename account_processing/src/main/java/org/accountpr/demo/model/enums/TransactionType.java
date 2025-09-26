package org.accountpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    PAYMENT("PAYMENT"),
    TRANSFER("TRANSFER"),
    WITHDRAWAL("WITHDRAWAL"),
    DEPOSIT("DEPOSIT");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }}
