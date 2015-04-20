package com.receiptofi.checkout.model;

import com.receiptofi.checkout.db.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/7/15 11:36 PM
 */
public class DataWrapper {

    private ProfileModel profileModel;
    private List<ReceiptItemModel> receiptItemModels = new ArrayList<>();
    private List<ReceiptModel> receiptModels = new ArrayList<>();
    private List<ExpenseTagModel> expenseTagModels = new ArrayList<>();
    private UnprocessedDocumentModel unprocessedDocumentModel;
    private List<NotificationModel> notificationModels = new ArrayList<>();
    private BillingAccountModel billingAccountModel;

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public void setProfileModel(ProfileModel profileModel) {
        this.profileModel = profileModel;
    }

    public List<ReceiptItemModel> getReceiptItemModels() {
        return receiptItemModels;
    }

    public void setReceiptItemModels(List<ReceiptItemModel> receiptItemModels) {
        this.receiptItemModels = receiptItemModels;
    }

    public List<ReceiptModel> getReceiptModels() {
        return receiptModels;
    }

    public void setReceiptModels(List<ReceiptModel> receiptModels) {
        this.receiptModels = receiptModels;
    }

    public List<ExpenseTagModel> getExpenseTagModels() {
        return expenseTagModels;
    }

    public void setExpenseTagModels(List<ExpenseTagModel> expenseTagModels) {
        this.expenseTagModels = expenseTagModels;
    }

    public UnprocessedDocumentModel getUnprocessedDocumentModel() {
        return unprocessedDocumentModel;
    }

    public void setUnprocessedDocumentModel(UnprocessedDocumentModel unprocessedDocumentModel) {
        this.unprocessedDocumentModel = unprocessedDocumentModel;
    }

    public List<NotificationModel> getNotificationModels() {
        return notificationModels;
    }

    public void setNotificationModels(List<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
    }

    public BillingAccountModel getBillingAccountModel() {
        return billingAccountModel;
    }

    public void setBillingAccountModel(BillingAccountModel billingAccountModel) {
        this.billingAccountModel = billingAccountModel;
    }
}
