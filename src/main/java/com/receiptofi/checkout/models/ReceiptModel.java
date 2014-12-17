package com.receiptofi.checkout.models;

import android.content.ContentValues;

import com.receiptofi.checkout.ReceiptofiApplication;

public class ReceiptModel {
    public String bizName;
    public String bizStoreAddress;
    public String bizStorePhone;
    public String date;
    public String expenseReport;
    public String filesBlobId;
    public String filesOrientation;
    public String filesSequence;
    public String id;
    public String notesText;
    public double ptax;
    public long rid;
    public double total;

    public boolean save() {
        ContentValues values = new ContentValues();
        values.put(ReceiptDB.Receipt.BIZ_NAME, bizName);
        values.put(ReceiptDB.Receipt.BIZ_STORE_ADDRESS, bizStoreAddress);
        values.put(ReceiptDB.Receipt.BIZ_STORE_PHONE, bizStorePhone);
        values.put(ReceiptDB.Receipt.DATE_R, date);
        values.put(ReceiptDB.Receipt.EXPENSE_REPORT, expenseReport);
        values.put(ReceiptDB.Receipt.FILES_BLOB, filesBlobId);
        values.put(ReceiptDB.Receipt.FILES_ORIENTATION, filesOrientation);
        values.put(ReceiptDB.Receipt.ID, id);
        values.put(ReceiptDB.Receipt.NOTES, notesText);
        values.put(ReceiptDB.Receipt.P_TAX, ptax);
        values.put(ReceiptDB.Receipt.R_ID, rid);
        values.put(ReceiptDB.Receipt.TOTAL, total);

        ReceiptofiApplication.rdh.getWritableDatabase().insert(ReceiptDB.Receipt.TABLE_NAME, null, values);
        return false;
    }
}
