package com.receiptofi.checkout.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroup {

    private List<List<ReceiptModel>> receiptGroup = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    public List<List<ReceiptModel>> getReceiptGroup() {
        return receiptGroup;
    }

    public void addReceiptGroup(List<ReceiptModel> receiptModel) {
        this.receiptGroup.add(receiptModel);
    }

    public List<ReceiptGroupHeader> getReceiptGroupHeaders() {
        return receiptGroupHeaders;
    }

    public void addReceiptGroupHeaders(ReceiptGroupHeader receiptGroupHeader) {
        this.receiptGroupHeaders.add(receiptGroupHeader);
    }
}
