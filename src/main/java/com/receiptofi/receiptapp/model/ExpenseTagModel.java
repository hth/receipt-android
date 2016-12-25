package com.receiptofi.receiptapp.model;

/**
 * User: hitender
 * Date: 1/23/15 11:13 PM
 */
public class ExpenseTagModel {

    private String id;
    private String tag;
    private String color;
    private boolean deleted;

    public ExpenseTagModel(String id, String tag, String color, boolean deleted) {
        this.id = id;
        this.tag = tag;
        this.color = color;
        this.deleted = deleted;
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

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return "ExpenseTagModel{" +
                "id='" + id + '\'' +
                ", tag='" + tag + '\'' +
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
        if (!tag.equals(that.tag)) return false;
        return color.equals(that.color);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + tag.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + (deleted ? 1 : 0);
        return result;
    }
}
