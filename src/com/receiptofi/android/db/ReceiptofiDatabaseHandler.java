package com.receiptofi.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.receiptofi.android.models.ReceiptDB;

public class ReceiptofiDatabaseHandler extends SQLiteOpenHelper {

    private static int DB_VERSION = 1;

    public ReceiptofiDatabaseHandler(Context context, String name) {
        super(context, name, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (db != null) {
            createTableReceipts(db);
            createTableImageIndex(db);
            createTableUploadQueue(db);
            createTableKeyValue(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTableReceipts(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.Receipt.TABLE_NAME + "("
                + ReceiptDB.Receipt.BIZ_NAME + " TEXT ,"
                + ReceiptDB.Receipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + ReceiptDB.Receipt.BIZ_STORE_PHONE + " TEXT ,"
                + ReceiptDB.Receipt.DATE_R + " TEXT ,"
                + ReceiptDB.Receipt.EXPENSE_REPORT + " TEXT ,"
                + ReceiptDB.Receipt.FILES_BLOB + " TEXT ,"
                + ReceiptDB.Receipt.FILES_ORIENTATION + " TEXT ,"
                + ReceiptDB.Receipt.ID + " TEXT ,"
                + ReceiptDB.Receipt.NOTES + " TEXT ,"
                + ReceiptDB.Receipt.P_TAX + " TEXT ,"
                + ReceiptDB.Receipt.R_ID + " TEXT ,"
                + ReceiptDB.Receipt.SEQUENCE + " TEXT ,"
                + ReceiptDB.Receipt.TOTAL + " TEXT " +

                ");");
    }

    private void createTableImageIndex(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.ImageIndex.TABLE_NAME + "("
                + ReceiptDB.ImageIndex.BLOB_ID + " TEXT ,"
                + ReceiptDB.ImageIndex.IMAGE_NAME + " TEXT " +

                ");");
    }

    private void createTableUploadQueue(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.UploadQueue.TABLE_NAME + "("
                + ReceiptDB.UploadQueue.IMAGE_DATE + " TEXT ,"
                + ReceiptDB.UploadQueue.IMAGE_NAME + " TEXT ,"
                + ReceiptDB.UploadQueue.STATUS + " TEXT " +

                ");");
    }

    private void createTableKeyValue(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.KeyVal.TABLE_NAME + "("
                + ReceiptDB.KeyVal.KEY + " TEXT ,"
                + ReceiptDB.KeyVal.VALUE + " TEXT " +

                ");");
    }

}
