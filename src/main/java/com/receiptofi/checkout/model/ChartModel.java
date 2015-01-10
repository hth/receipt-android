package com.receiptofi.checkout.model;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/10/15 7:18 AM
 */
public class ChartModel {

    private List<ReceiptModel> receiptModels = new ArrayList<>();
    private double total = 0.0;

    public List<ReceiptModel> getReceiptModels() {
        return receiptModels;
    }

    public void addReceiptModel(ReceiptModel receiptModel) {
        this.receiptModels.add(receiptModel);
    }

    public double getTotal() {
        return total;
    }

    public void addTotal(double total) {
        this.total += total;
    }
}
