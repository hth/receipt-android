package com.receiptofi.receiptapp.model;

/**
 * User: hitender
 * Date: 12/2/15 5:16 PM
 */
public class ItemReceiptModel {

    private String receiptId;
    private String bizName;
    private String lat;
    private String lng;
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
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getBizName() {
        return bizName;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
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
