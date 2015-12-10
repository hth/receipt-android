package com.receiptofi.receiptapp.model.types;

/**
 * User: hitender
 * Date: 12/10/15 12:16 AM
 */
public enum DistanceUnit {
    M("M", "Miles"),
    K("K", "Kilometers"),
    N("N", "Nautical");

    private final String description;
    private final String name;

    DistanceUnit(String name, String description) {
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
