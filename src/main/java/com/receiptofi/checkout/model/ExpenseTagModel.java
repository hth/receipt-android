package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 1/23/15 11:13 PM
 */
public class ExpenseTagModel {

    private String id;
    private String tag;
    private String color;

    public ExpenseTagModel(String id, String tag, String color) {
        this.id = id;
        this.tag = tag;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "ExpenseTagModel{" +
                "id='" + id + '\'' +
                ", tag='" + tag + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
