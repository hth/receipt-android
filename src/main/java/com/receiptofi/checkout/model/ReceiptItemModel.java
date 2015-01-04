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

    public ReceiptItemModel(String id, String name, String price, String quantity, String receiptId, String sequence, String tax) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.receiptId = receiptId;
        this.sequence = sequence;
        this.tax = tax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
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
                '}';
    }
}
