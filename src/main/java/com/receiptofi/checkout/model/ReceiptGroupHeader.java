package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 1/2/15 12:45 PM
 */
public class ReceiptGroupHeader {

    private String month;
    private String year;
    private Double total;
    private int count;

    public ReceiptGroupHeader(String month, String year, Double total, int count) {
        this.month = month;
        this.year = year;
        this.total = total;
        this.count = count;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public Double getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "ReceiptGroupHeader{" +
                "month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", total=" + total +
                ", count=" + count +
                '}';
    }
}
