package org.clientpr.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ClientRole {
    CURRENT_CLIENT("CURRENT_CLIENT"),
    BLOCKED_CLIENT("BLOCKED_CLIENT");

    private final String value;

    ClientRole(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
