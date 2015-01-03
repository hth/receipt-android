package com.receiptofi.checkout.models;

/**
 * Created by hitender on 1/2/15.
 */
public class ReceiptGroupHeader {

    private String month;
    private String year;
    private double gross;
    private int count;

    public ReceiptGroupHeader(String month, String year) {
        this.month = month;
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public double getGross() {
        return gross;
    }

    public void updateGross(double gross) {
        this.gross =+ gross;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        this.count =+ 1;
    }

    public void decreaseCount() {
        this.count =+ 1;
    }
}
