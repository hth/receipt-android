package com.receiptofi.checkout.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReceiptofiDatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = ReceiptofiDatabaseHandler.class.getSimpleName();

    private static int DB_VERSION = 1;
    private SQLiteDatabase db = null;

    public ReceiptofiDatabaseHandler(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    /**
     * Do not initialize db here.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "executing onCreate");
        if (db != null) {
            this.db = db;
            createTableReceipts();
            createTableImageIndex();
            createTableUploadQueue();
            createTableKeyValue();
            createTableMonthlyReport();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTableReceipts() {
        Log.d(TAG, "executing createTableReceipts");
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

    public void createTableImageIndex() {
        Log.d(TAG, "executing createTableImageIndex");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.ImageIndex.TABLE_NAME + "("
                + ReceiptDB.ImageIndex.BLOB_ID + " TEXT ,"
                + ReceiptDB.ImageIndex.IMAGE_PATH + " TEXT " +

                ");");
    }

    public void createTableUploadQueue() {
        Log.d(TAG, "executing createTableUploadQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.UploadQueue.TABLE_NAME + "("
                + ReceiptDB.UploadQueue.IMAGE_DATE + " TEXT ,"
                + ReceiptDB.UploadQueue.IMAGE_PATH + " TEXT UNIQUE ,"
                + ReceiptDB.UploadQueue.STATUS + " TEXT " +

                ");");
    }

    public void createTableKeyValue() {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.KeyVal.TABLE_NAME + "("
                + ReceiptDB.KeyVal.KEY + " TEXT ,"
                + ReceiptDB.KeyVal.VALUE + " TEXT " +

                ");");
    }

    public void createTableMonthlyReport() {
        Log.d(TAG, "executing createTableMonthlyReports");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ReceiptDB.MonthlyReport.TABLE_NAME + "("
                + ReceiptDB.MonthlyReport.MONTH + " TEXT ,"
                + ReceiptDB.MonthlyReport.YEAR + " TEXT ,"
                + ReceiptDB.MonthlyReport.TOTAL_AMT + " TEXT ,"
                + ReceiptDB.MonthlyReport.RECEIPT_COUNT + " TEXT " +
                ");");
    }
}
