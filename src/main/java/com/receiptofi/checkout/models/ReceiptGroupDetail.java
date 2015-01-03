package com.receiptofi.checkout.models;

import java.util.Date;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroupDetail {

    private String receiptId;
    private Date receiptTransaction;
    private String businessName;
    private double total;

    public ReceiptGroupDetail(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public Date getReceiptTransaction() {
        return receiptTransaction;
    }

    public void setReceiptTransaction(Date receiptTransaction) {
        this.receiptTransaction = receiptTransaction;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
