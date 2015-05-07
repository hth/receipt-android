package com.receiptofi.checkout.model.types;

/**
 * User: hitender
 * Date: 5/7/15 5:38 AM
 */
public enum AccountBillingType {
    P("P", "Promotion", 1000),
    NB("NB", "No Billing", 0),
    M10("M10", "Monthly 10", 10),
    M30("M30", "Monthly 30", 30),
    M40("M40", "Monthly 40", 40),
    M50("M50", "Monthly 50", 50),
    A360("A360", "Annual 360", 360);

    private final String description;
    private final String name;
    private final int allowedDocumentsPerMonth;

    AccountBillingType(String name, String description, int allowedDocumentsPerMonth) {
        this.name = name;
        this.description = description;
        this.allowedDocumentsPerMonth = allowedDocumentsPerMonth;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getAllowedDocumentsPerMonth() {
        return allowedDocumentsPerMonth;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
