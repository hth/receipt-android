package com.receiptofi.checkout.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private static int DB_VERSION = 1;
    private SQLiteDatabase db = null;

    public DatabaseHandler(Context context, String name) {
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.Receipt.TABLE_NAME + "("
                + DatabaseTable.Receipt.BIZ_NAME + " TEXT ,"
                + DatabaseTable.Receipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + DatabaseTable.Receipt.BIZ_STORE_PHONE + " TEXT ,"
                + DatabaseTable.Receipt.DATE_R + " TEXT ,"
                + DatabaseTable.Receipt.EXPENSE_REPORT + " TEXT ,"
                + DatabaseTable.Receipt.FILES_BLOB + " TEXT ,"
                + DatabaseTable.Receipt.FILES_ORIENTATION + " TEXT ,"
                + DatabaseTable.Receipt.ID + " TEXT ,"
                + DatabaseTable.Receipt.NOTES + " TEXT ,"
                + DatabaseTable.Receipt.P_TAX + " TEXT ,"
                + DatabaseTable.Receipt.R_ID + " TEXT ,"
                + DatabaseTable.Receipt.SEQUENCE + " TEXT ,"
                + DatabaseTable.Receipt.TOTAL + " TEXT " +

                ");");
    }

    public void createTableImageIndex() {
        Log.d(TAG, "executing createTableImageIndex");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.ImageIndex.TABLE_NAME + "("
                + DatabaseTable.ImageIndex.BLOB_ID + " TEXT ,"
                + DatabaseTable.ImageIndex.IMAGE_PATH + " TEXT " +

                ");");
    }

    public void createTableUploadQueue() {
        Log.d(TAG, "executing createTableUploadQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.UploadQueue.TABLE_NAME + "("
                + DatabaseTable.UploadQueue.IMAGE_DATE + " TEXT ,"
                + DatabaseTable.UploadQueue.IMAGE_PATH + " TEXT UNIQUE ,"
                + DatabaseTable.UploadQueue.STATUS + " TEXT " +

                ");");
    }

    public void createTableKeyValue() {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.KeyValue.TABLE_NAME + "("
                + DatabaseTable.KeyValue.KEY + " TEXT ,"
                + DatabaseTable.KeyValue.VALUE + " TEXT " +

                ");");
    }

    public void createTableMonthlyReport() {
        Log.d(TAG, "executing createTableMonthlyReports");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DatabaseTable.MonthlyReport.TABLE_NAME + "("
                + DatabaseTable.MonthlyReport.MONTH + " TEXT ,"
                + DatabaseTable.MonthlyReport.YEAR + " TEXT ,"
                + DatabaseTable.MonthlyReport.TOTAL_AMT + " TEXT ,"
                + DatabaseTable.MonthlyReport.RECEIPT_COUNT + " TEXT " +
                ");");
    }
}
