package com.receiptofi.receiptapp.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: hitender
 * Date: 12/5/15 5:48 PM
 */
public class ShoppingItemModel {
    private String name;
    private String customName;
    private String bizName;
    private int count;
    private boolean checked;

    /**
     * count times multiplier.
     */
    private double smoothCount;

    public ShoppingItemModel(String name, String bizName, int count, double multiplier) {
        this.name = name;
        this.bizName = bizName;
        this.count = count;
        this.smoothCount = count * multiplier;
    }

    public String getName() {
        return name;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getBizName() {
        return bizName;
    }

    public int getCount() {
        return count;
    }

    public double getSmoothCount() {
        return smoothCount;
    }

    public boolean isChecked() {
        return checked;
    }

    public void checked() {
        this.checked = true;
    }

    public void unChecked() {
        this.checked = false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("smoothCount", smoothCount)
                .toString();
    }
}
