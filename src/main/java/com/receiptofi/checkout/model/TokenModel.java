package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 7/6/15 11:35 AM
 */
public class TokenModel {
    private String token;
    private boolean hasCustomerInfo;
    private String firstName;
    private String lastName;
    private String postalCode;
    private String planId;

    public TokenModel(
            String token,
            boolean hasCustomerInfo,
            String firstName,
            String lastName,
            String postalCode,
            String planId
    ) {
        this.token = token;
        this.hasCustomerInfo = hasCustomerInfo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
        this.planId = planId;
    }

    public String getToken() {
        return token;
    }

    public boolean isHasCustomerInfo() {
        return hasCustomerInfo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPlanId() {
        return planId;
    }
}
