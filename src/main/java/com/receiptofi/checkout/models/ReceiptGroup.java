package com.receiptofi.checkout.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroup {
    
    List<ReceiptGroupDetail> receiptGroupDetails = new LinkedList<>();
    List<List<ReceiptGroupHeader>> receiptGroupHeader = new LinkedList<>();

    public List<ReceiptGroupDetail> getReceiptGroupDetails() {
        return receiptGroupDetails;
    }

    public void addReceiptGroupDetails(ReceiptGroupDetail receiptGroupDetails) {
        this.receiptGroupDetails.add(receiptGroupDetails);
    }

    public List<List<ReceiptGroupHeader>> getReceiptGroupHeader() {
        return receiptGroupHeader;
    }

    public void addReceiptGroupHeader(List<ReceiptGroupHeader> receiptGroupHeader) {
        this.receiptGroupHeader.add(receiptGroupHeader);
    }
}
