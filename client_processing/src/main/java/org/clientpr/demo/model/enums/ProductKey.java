package org.clientpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum ProductKey {
    DC("DC"),
    CC("CC"),
    PENS("PENS"),
    NS("NS"),
    AC("AC"),
    IPO("IPO"),
    PC("PC"),
    INS("INS"),
    BS("BS");

    private final String value;

    ProductKey(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
