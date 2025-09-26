package org.accountpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED"),
    BLOCKED("BLOCKED"),
    ARRESTED("ARRESTED");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
