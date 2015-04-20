package com.receiptofi.checkout.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 4/19/15 6:19 PM
 */
public class BillingAccountModel {

    private String accountBillingType;
    private boolean billedAccount;
    private List<BillingHistoryModel> billingHistories = new ArrayList<>();

    public BillingAccountModel(String accountBillingType, boolean billedAccount) {
        this.accountBillingType = accountBillingType;
        this.billedAccount = billedAccount;
    }

    public String getAccountBillingType() {
        return accountBillingType;
    }

    public boolean isBilledAccount() {
        return billedAccount;
    }

    public List<BillingHistoryModel> getBillingHistories() {
        return billingHistories;
    }

    public void setBillingHistories(List<BillingHistoryModel> billingHistories) {
        this.billingHistories = billingHistories;
    }
}
