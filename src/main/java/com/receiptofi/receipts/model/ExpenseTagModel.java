package com.receiptofi.receipts.model;

/**
 * User: hitender
 * Date: 1/23/15 11:13 PM
 */
public class ExpenseTagModel {

    private String id;
    private String name;
    private String color;
    private boolean deleted;

    public ExpenseTagModel(String id, String name, String color, boolean deleted) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.deleted = deleted;
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

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return "ExpenseTagModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpenseTagModel that = (ExpenseTagModel) o;

        if (deleted != that.deleted) return false;
        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        return color.equals(that.color);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + (deleted ? 1 : 0);
        return result;
    }
}
