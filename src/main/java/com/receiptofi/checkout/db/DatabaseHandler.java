package com.receiptofi.checkout.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.receiptofi.checkout.db.DatabaseTable.*;

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
        if (null != db) {
            this.db = db;
            createTableReceipts();
            createTableImageIndex();
            createTableUploadQueue();
            createTableKeyValue();
            createTableMonthlyReport();
            //populateAllTablesWithDummyData();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTableReceipts() {
        Log.d(TAG, "executing createTableReceipts");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + Receipt.TABLE_NAME + "("
                + Receipt.BIZ_NAME + " TEXT ,"
                + Receipt.BIZ_STORE_ADDRESS + " TEXT ,"
                + Receipt.BIZ_STORE_PHONE + " TEXT ,"
                + Receipt.DATE + " TEXT ,"
                + Receipt.EXPENSE_REPORT + " TEXT ,"
                + Receipt.BLOB_IDS + " TEXT ,"
                + Receipt.ID + " TEXT UNIQUE ,"
                + Receipt.NOTES + " TEXT ,"
                + Receipt.PTAX + " DOUBLE ,"
                + Receipt.RID + " TEXT ,"
                + Receipt.TOTAL + " DOUBLE " +

                ");");
    }

    public void createTableImageIndex() {
        Log.d(TAG, "executing createTableImageIndex");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ImageIndex.TABLE_NAME + "("
                + ImageIndex.BLOB_ID + " TEXT ,"
                + ImageIndex.IMAGE_PATH + " TEXT " +

                ");");
    }

    public void createTableUploadQueue() {
        Log.d(TAG, "executing createTableUploadQueue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + UploadQueue.TABLE_NAME + "("
                + UploadQueue.IMAGE_DATE + " TEXT ,"
                + UploadQueue.IMAGE_PATH + " TEXT UNIQUE ,"
                + UploadQueue.STATUS + " TEXT " +

                ");");
    }

    public void createTableKeyValue() {
        Log.d(TAG, "executing createTableKeyValue");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + KeyValue.TABLE_NAME + "("
                + KeyValue.KEY + " TEXT ,"
                + KeyValue.VALUE + " TEXT " +

                ");");
    }

    public void createTableMonthlyReport() {
        Log.d(TAG, "executing createTableMonthlyReports");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MonthlyReport.TABLE_NAME + "("
                + MonthlyReport.MONTH + " TEXT ,"
                + MonthlyReport.YEAR + " TEXT ,"
                + MonthlyReport.TOTAL + " DOUBLE ,"
                + MonthlyReport.COUNT + " INT " +

                ");");
    }
}
