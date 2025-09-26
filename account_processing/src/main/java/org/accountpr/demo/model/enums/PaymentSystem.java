package org.accountpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum PaymentSystem {
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
    MIR("MIR"),
    AMEX("AMEX");

    private final String value;

    PaymentSystem(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
