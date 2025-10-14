package org.clientpr.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    USER("USER"),
    GRAND_EMPLOYEE("GRAND_EMPLOYEE"),
    MASTER("MASTER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
