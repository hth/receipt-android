package com.receiptofi.checkout.model;

public class ReceiptModel {
    public static final String NULL = "null";

    private String bizName;
    private String address;
    private String phone;
    private String date;
    private String expenseReport;
    private String blobIds;
    private String id;
    private String notes;
    private double ptax;
    private String rid;
    private double total;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        if (!bizName.equalsIgnoreCase(NULL)) {
            this.bizName = bizName;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!address.equalsIgnoreCase(NULL)) {
            this.address = address;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (!phone.equalsIgnoreCase(NULL)) {
            this.phone = phone;
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        if (!date.equalsIgnoreCase(NULL)) {
            this.date = date;
        }
    }

    public String getExpenseReport() {
        return expenseReport;
    }

    public void setExpenseReport(String expenseReport) {
        if (!expenseReport.equalsIgnoreCase(NULL)) {
            this.expenseReport = expenseReport;
        }
    }

    public String getBlobIds() {
        return blobIds;
    }

    public void setBlobIds(String blobIds) {
        if (!blobIds.equalsIgnoreCase(NULL)) {
            this.blobIds = blobIds;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (!id.equalsIgnoreCase(NULL)) {
            this.id = id;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (!notes.equalsIgnoreCase(NULL)) {
            this.notes = notes;
        }
    }

    public double getPtax() {
        return ptax;
    }

    public void setPtax(double ptax) {
        this.ptax = ptax;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        if (!rid.equalsIgnoreCase(NULL)) {
            this.rid = rid;
        }
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
