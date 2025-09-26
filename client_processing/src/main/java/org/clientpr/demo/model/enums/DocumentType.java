package org.clientpr.demo.model.enums;

import lombok.Getter;

@Getter
public enum DocumentType {
    PASSPORT("PASSPORT"),
    INT_PASSPORT("INT_PASSPORT"),
    BIRTH_CERT("BIRTH_CERT");

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
