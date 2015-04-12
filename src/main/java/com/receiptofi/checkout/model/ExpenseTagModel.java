package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 1/23/15 11:13 PM
 */
public class ExpenseTagModel {

    private String id;
    private String name;
    private String color;

    public ExpenseTagModel(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "ExpenseTagModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
