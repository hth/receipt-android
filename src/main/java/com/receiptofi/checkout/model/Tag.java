package com.receiptofi.checkout.model;

/**
 * Created by kevin on 5/26/15.
 */
public class Tag {
    public Tag(String tag, int color) {
        this.tag = tag;
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String tag;


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int color;
}
