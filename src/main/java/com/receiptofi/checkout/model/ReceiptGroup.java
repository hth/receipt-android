package com.receiptofi.checkout.model;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/2/15 6:44 AM
 */
public class ReceiptGroup {
    private static final String TAG = ReceiptGroup.class.getSimpleName();

    private List<List<ReceiptModel>> receiptModels = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    private ReceiptGroup() {}

    public static ReceiptGroup getInstance() {
        return new ReceiptGroup();
    }

    public List<List<ReceiptModel>> getReceiptModels() {
        return receiptModels;
    }

    public List<ReceiptGroupHeader> getReceiptGroupHeaders() {
        return receiptGroupHeaders;
    }

    public void addReceiptGroupHeader(ReceiptGroupHeader receiptGroupHeader) {
        this.receiptGroupHeaders.add(receiptGroupHeader);
    }

    public void addReceiptGroup(List<ReceiptModel> receiptModel) {
        this.receiptModels.add(receiptModel);
    }
}
