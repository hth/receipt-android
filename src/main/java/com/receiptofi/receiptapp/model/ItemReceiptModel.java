package com.receiptofi.receiptapp.model;

import android.util.Log;

/**
 * De-Normalized view of Item and Receipt table for computing shopping list.
 * User: hitender
 * Date: 12/2/15 5:16 PM
 */
public class ItemReceiptModel {
    private static final String TAG = ItemReceiptModel.class.getSimpleName();

    private String receiptId;
    private String bizName;
    private Double lat;
    private Double lng;
    private String receiptDate;
    private String expenseTagId;
    private boolean active;
    private boolean deleted;

    private String itemId;
    private String name;
    private Double price;
    private String quantity;
    private String tax;

    public ItemReceiptModel(ReceiptModel receipt, ReceiptItemModel item) {
        try {
            Log.i(TAG, receipt.toString());
            this.receiptId = receipt.getId();
            this.bizName = receipt.getBizName();
            this.lat = receipt.getLat();
            this.lng = receipt.getLng();
            this.receiptDate = receipt.getReceiptDate();
            this.expenseTagId = receipt.getExpenseTagId();
            this.active = receipt.isActive();
            this.deleted = receipt.isDeleted();

            this.itemId = item.getId();
            this.name = item.getName();
            this.price = item.getPrice();
            this.quantity = item.getQuantity();
            this.tax = item.getTax();
        } catch (NullPointerException npe) {
            //TODO make sure to consider split receipt here.
            npe.getStackTrace();
        }
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getBizName() {
        return bizName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public String getExpenseTagId() {
        return expenseTagId;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getTax() {
        return tax;
    }
}
