package com.receiptofi.receipts.model;

/**
 * User: hitender
 * Date: 7/9/15 1:54 AM
 */
public class TransactionDetailSubscriptionModel implements TransactionDetail {
    private final TYPE type;
    private final boolean success;
    private final String status;
    private final String planId;
    private final String firstName;
    private final String lastName;
    private final String postalCode;
    private final String accountPlanId;
    private final String subscriptionId;
    private final String message;

    public TransactionDetailSubscriptionModel(
            TYPE type,
            boolean success,
            String status,
            String planId,
            String firstName,
            String lastName,
            String postalCode,
            String accountPlanId,
            String subscriptionId,
            String message
    ) {
        this.type = type;
        this.success = success;
        this.status = status;
        this.planId = planId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
        this.accountPlanId = accountPlanId;
        this.subscriptionId = subscriptionId;
        this.message = message;
    }

    public TYPE getType() {
        return type;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }

    public String getPlanId() {
        return planId;
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

    public String getAccountPlanId() {
        return accountPlanId;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getMessage() {
        return message;
    }
}
