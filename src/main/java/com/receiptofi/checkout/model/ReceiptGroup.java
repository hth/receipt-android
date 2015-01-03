package com.receiptofi.checkout.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroup {

    private List<List<ReceiptGroupDetail>> receiptGroupDetails = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    public List<List<ReceiptGroupDetail>> getReceiptGroupDetails() {
        return receiptGroupDetails;
    }

    public void addReceiptGroupDetails(List<ReceiptGroupDetail> receiptGroupDetails) {
        this.receiptGroupDetails.add(receiptGroupDetails);
    }

    public List<ReceiptGroupHeader> getReceiptGroupHeaders() {
        return receiptGroupHeaders;
    }

    public void addReceiptGroupHeaders(ReceiptGroupHeader receiptGroupHeader) {
        this.receiptGroupHeaders.add(receiptGroupHeader);
    }
}
