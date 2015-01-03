package com.receiptofi.checkout.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroup {

    private List<List<ReceiptGroupDetail>> receiptGroupDetails = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    public ReceiptGroup() {
        ReceiptGroupDetail receiptGroupDetail = new ReceiptGroupDetail("1234");
        receiptGroupDetail.setBusinessName("Costco");
        receiptGroupDetail.setReceiptId("xys");
        receiptGroupDetail.setTotal(12.00);
        List<ReceiptGroupDetail> receiptGroupDetailList = new ArrayList<>();
        receiptGroupDetailList.add(receiptGroupDetail);
        addReceiptGroupDetails(receiptGroupDetailList);

        ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader("Jan", "2015");
        receiptGroupHeader.increaseCount();
        receiptGroupHeader.addTotal(12.00);
        addReceiptGroupHeaders(receiptGroupHeader);
    }

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
