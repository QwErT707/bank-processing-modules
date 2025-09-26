package org.accountpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum PaymentType {
    TRANSFER("TRANSFER"),
    DEPOSIT("DEPOSIT"),
    WITHDRAWAL("WITHDRAWAL"),
    PURCHASE("PURCHASE"),
    FEE("FEE"),
    INTEREST("INTEREST");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
