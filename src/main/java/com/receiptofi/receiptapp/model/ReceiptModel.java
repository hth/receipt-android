package com.receiptofi.receiptapp.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReceiptModel {
    public static final String NULL = "null";

    private String bizName;
    private String address;
    private String phone;
    private Double lat;
    private Double lng;
    private String type;
    private Double rating;
    private String receiptDate;
    private String expenseReport;
    private String blobIds;
    private String id;
    private String notes;
    private double ptax;
    private double tax;
    private String rid;
    private double total;
    private List<ReceiptItemModel> receiptItems = new LinkedList<>();
    private String billStatus;
    private String expenseTagId;
    private String referReceiptId;
    private int splitCount;
    private double splitTotal;
    private double splitTax;
    private List<ReceiptSplitModel> receiptSplitModels = new ArrayList<>();
    private boolean active;
    private boolean deleted;

    private ExpenseTagModel expenseTagModel;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        if (null != bizName && !bizName.equalsIgnoreCase(NULL)) {
            this.bizName = bizName;
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (null != address && !address.equalsIgnoreCase(NULL)) {
            this.address = address;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (null != phone && !phone.equalsIgnoreCase(NULL)) {
            this.phone = phone;
        }
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        if (null != receiptDate && !receiptDate.equalsIgnoreCase(NULL)) {
            this.receiptDate = receiptDate;
        }
    }

    public String getExpenseReport() {
        return expenseReport;
    }

    public void setExpenseReport(String expenseReport) {
        if (null != expenseReport && !expenseReport.equalsIgnoreCase(NULL)) {
            this.expenseReport = expenseReport;
        }
    }

    public String getBlobIds() {
        return blobIds;
    }

    public void setBlobIds(String blobIds) {
        if (null != blobIds && !blobIds.equalsIgnoreCase(NULL)) {
            this.blobIds = blobIds;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (null != id && !id.equalsIgnoreCase(NULL)) {
            this.id = id;
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        if (null != notes && !notes.equalsIgnoreCase(NULL)) {
            this.notes = notes;
        }
    }

    public double getPtax() {
        return ptax;
    }

    public void setPtax(double ptax) {
        this.ptax = ptax;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        if (null != rid && !rid.equalsIgnoreCase(NULL)) {
            this.rid = rid;
        }
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ReceiptItemModel> getReceiptItems() {
        return receiptItems;
    }

    public void setReceiptItems(List<ReceiptItemModel> receiptItems) {
        this.receiptItems = receiptItems;
    }

    public void addReceiptItem(ReceiptItemModel receiptItem) {
        this.receiptItems.add(receiptItem);
    }

    public String getExpenseTagId() {
        return expenseTagId;
    }

    public void setExpenseTagId(String expenseTagId) {
        this.expenseTagId = expenseTagId;
    }

    public String getReferReceiptId() {
        return referReceiptId;
    }

    public void setReferReceiptId(String referReceiptId) {
        if (null != referReceiptId && !referReceiptId.equalsIgnoreCase(NULL)) {
            this.referReceiptId = referReceiptId;
        }
    }

    public int getSplitCount() {
        return splitCount;
    }

    public void setSplitCount(int splitCount) {
        this.splitCount = splitCount;
    }

    public double getSplitTotal() {
        return splitTotal;
    }

    public void setSplitTotal(double splitTotal) {
        this.splitTotal = splitTotal;
    }

    public double getSplitTax() {
        return splitTax;
    }

    public void setSplitTax(double splitTax) {
        this.splitTax = splitTax;
    }

    public List<ReceiptSplitModel> getReceiptSplitModels() {
        return receiptSplitModels;
    }

    public void setReceiptSplitModels(List<ReceiptSplitModel> receiptSplitModels) {
        this.receiptSplitModels = receiptSplitModels;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getReceiptYearMonth() {
        return receiptDate.substring(0, 7);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public ExpenseTagModel getExpenseTagModel() {
        return expenseTagModel;
    }

    public void setExpenseTagModel(ExpenseTagModel expenseTagModel) {
        this.expenseTagModel = expenseTagModel;
    }

    @Override
    public String toString() {
        return "ReceiptModel{" +
                "bizName='" + bizName + '\'' +
                ", splitTotal=" + splitTotal +
                ", rid='" + rid + '\'' +
                '}';
    }
}
