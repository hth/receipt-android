package com.receiptofi.checkout.model;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/2/15 6:44 AM
 */
public class ReceiptGroup {

    private List<List<ReceiptModel>> receiptModels = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    public List<List<ReceiptModel>> getReceiptModels() {
        return receiptModels;
    }

    public void addReceiptGroup(List<ReceiptModel> receiptModel) {
        this.receiptModels.add(receiptModel);
    }

    public List<ReceiptGroupHeader> getReceiptGroupHeaders() {
        return receiptGroupHeaders;
    }

    public void addReceiptGroupHeader(ReceiptGroupHeader receiptGroupHeader) {
        this.receiptGroupHeaders.add(receiptGroupHeader);
    }
}
