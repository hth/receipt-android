package com.receiptofi.checkout.models;

import android.content.ContentValues;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;

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
        values.put(DatabaseTable.Receipt.BIZ_NAME, bizName);
        values.put(DatabaseTable.Receipt.BIZ_STORE_ADDRESS, bizStoreAddress);
        values.put(DatabaseTable.Receipt.BIZ_STORE_PHONE, bizStorePhone);
        values.put(DatabaseTable.Receipt.DATE_R, date);
        values.put(DatabaseTable.Receipt.EXPENSE_REPORT, expenseReport);
        values.put(DatabaseTable.Receipt.FILES_BLOB, filesBlobId);
        values.put(DatabaseTable.Receipt.FILES_ORIENTATION, filesOrientation);
        values.put(DatabaseTable.Receipt.ID, id);
        values.put(DatabaseTable.Receipt.NOTES, notesText);
        values.put(DatabaseTable.Receipt.P_TAX, ptax);
        values.put(DatabaseTable.Receipt.R_ID, rid);
        values.put(DatabaseTable.Receipt.TOTAL, total);

        ReceiptofiApplication.RDH.getWritableDatabase().insert(DatabaseTable.Receipt.TABLE_NAME, null, values);
        return false;
    }
}
