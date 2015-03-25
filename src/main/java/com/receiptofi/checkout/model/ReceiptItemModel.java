package com.receiptofi.checkout.model;

/**
 * Created by hitender on 1/4/15.
 */
public class ReceiptItemModel {

    private String id;
    private String name;
    private String price;
    private String quantity;
    private String receiptId;
    private String sequence;
    private String tax;
    private String expenseTagId;

    public ReceiptItemModel(String id, String name, String price, String quantity, String receiptId, String sequence, String tax, String expenseTagId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.receiptId = receiptId;
        this.sequence = sequence;
        this.tax = tax;
        this.expenseTagId = expenseTagId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getSequence() {
        return sequence;
    }

    public String getTax() {
        return tax;
    }

    public String getExpenseTagId() {
        return expenseTagId;
    }

    @Override
    public String toString() {
        return "ReceiptItemModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", receiptId='" + receiptId + '\'' +
                ", sequence='" + sequence + '\'' +
                ", tax='" + tax + '\'' +
                ", expenseTagId='" + expenseTagId + '\'' +
                '}';
    }
}
