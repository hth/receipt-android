package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 4/19/15 6:19 PM
 */
public class BillingHistoryModel {

    private String id;
    private String billedForMonth;
    private String billedStatus;
    private String accountBillingType;
    private String billedDate;

    public BillingHistoryModel(String id, String billedForMonth, String billedStatus, String accountBillingType, String billedDate) {
        this.id = id;
        this.billedForMonth = billedForMonth;
        this.billedStatus = billedStatus;
        this.accountBillingType = accountBillingType;
        this.billedDate = billedDate;
    }

    public String getId() {
        return id;
    }

    public String getBilledForMonth() {
        return billedForMonth;
    }

    public String getBilledStatus() {
        return billedStatus;
    }

    public String getAccountBillingType() {
        return accountBillingType;
    }

    public String getBilledDate() {
        return billedDate;
    }
}
