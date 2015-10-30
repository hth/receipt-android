package com.receiptofi.receiptapp.model;

/**
 * User: hitender
 * Date: 10/18/15 5:23 PM
 */
public class ReceiptSplitModel {

    private String id;
    private String rid;
    private String initials;
    private String name;

    public ReceiptSplitModel(String id, String rid, String initials, String name) {
        this.id = id;
        this.rid = rid;
        this.initials = initials;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getRid() {
        return rid;
    }

    public String getInitials() {
        return initials;
    }

    public String getName() {
        return name;
    }
}
