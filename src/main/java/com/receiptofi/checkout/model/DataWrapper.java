package com.receiptofi.checkout.model;

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
}
