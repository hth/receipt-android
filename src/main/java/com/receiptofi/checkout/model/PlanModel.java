package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 6/28/15 9:46 AM
 */
public class PlanModel {

    private String accountBillingType;
    private String billingDayOfMonth;
    private String billingFrequency;
    private String planDescription;
    private String planId;
    private String name;
    private String paymentGateway;
    private String price;

    // needed for view
    public PlanModel() {

    }

    public PlanModel(String accountBillingType, String billingDayOfMonth, String billingFrequency, String planDescription, String planId, String name, String paymentGateway, String price) {
        this.accountBillingType = accountBillingType;
        this.billingDayOfMonth = billingDayOfMonth;
        this.billingFrequency = billingFrequency;
        this.planDescription = planDescription;
        this.planId = planId;
        this.name = name;
        this.paymentGateway = paymentGateway;
        this.price = price;
    }

    public String getAccountBillingType() {
        return accountBillingType;
    }

    public String getBillingDayOfMonth() {
        return billingDayOfMonth;
    }

    public String getBillingFrequency() {
        return billingFrequency;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public String getPlanId() {
        return planId;
    }

    public String getName() {
        return name;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public String getPrice() {
        return price;
    }
}
