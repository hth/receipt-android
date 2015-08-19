package com.receiptofi.receiptapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/19/15 6:19 PM
 */
public class BillingAccountModel {

    private String accountBillingType;
    private List<BillingHistoryModel> billingHistories = new ArrayList<>();

    // needed for view
    public BillingAccountModel() {
    }

    public BillingAccountModel(String accountBillingType) {
        this.accountBillingType = accountBillingType;
    }

    public String getAccountBillingType() {
        return accountBillingType;
    }

    public List<BillingHistoryModel> getBillingHistories() {
        return billingHistories;
    }

    public void setBillingHistories(List<BillingHistoryModel> billingHistories) {
        if (billingHistories != null) {
            this.billingHistories = billingHistories;
        }
    }

    public String displayBillingType() {
        return accountBillingType;
    }
}
