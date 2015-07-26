package com.receiptofi.receipts.http.types;

/**
 * User: hitender
 * Date: 6/26/15 3:29 AM
 */
public enum ExpenseTagSwipe {
    EDIT(0),
    DELETE(1);

    private int code;

    ExpenseTagSwipe(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ExpenseTagSwipe findSwipeTypeByCode(int code) {
        for (ExpenseTagSwipe t : ExpenseTagSwipe.values()) {
            if (t.getCode() == code) {
                return t;
            }
        }
        return null;
    }
}
