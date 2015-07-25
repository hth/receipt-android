package com.receiptofi.receipts.model;

/**
 * User: hitender
 * Date: 7/9/15 1:52 AM
 */
public class TransactionDetailPaymentModel implements TransactionDetail {
    private final TYPE type;
    private final boolean success;
    private final String status;
    private final String firstName;
    private final String lastName;
    private final String postalCode;
    private final String accountPlanId;
    private final String transactionId;
    private final String message;

    public TransactionDetailPaymentModel(
            TYPE type,
            boolean success,
            String status,
            String firstName,
            String lastName,
            String postalCode,
            String accountPlanId,
            String transactionId,
            String message
    ) {
        this.type = type;
        this.success = success;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.postalCode = postalCode;
        this.accountPlanId = accountPlanId;
        this.transactionId = transactionId;
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

    public String getTransactionId() {
        return transactionId;
    }

    public String getMessage() {
        return message;
    }
}
