package com.receiptofi.checkout.model.types;

/**
 * User: hitender
 * Date: 5/7/15 4:41 AM
 */
public enum BilledStatus {
    NB("NB", "Not Billed"),
    P("P", "Promotion"),
    B("B", "Billed");

    private final String description;
    private final String name;

    BilledStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
