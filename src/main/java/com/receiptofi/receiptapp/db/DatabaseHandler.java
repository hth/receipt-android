package com.receiptofi.receiptapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private static int DB_VERSION = 1;
    private static DatabaseHandler dbInstance;
    private SQLiteDatabase db = null;

    private DatabaseHandler(Context context) {
        super(context, DatabaseTable.DB_NAME, null, DB_VERSION);
    }

    public static DatabaseHandler getsInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DatabaseHandler(context);
        }
        return dbInstance;
    }

    /**
     * Do not initialize db here.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DatabaseHandler onCreate");
        if (null != db) {
            this.db = db;
            Log.d(TAG, "creating all tables");
            CreateTable.createTableReceipts(db);
            CreateTable.createTableImageIndex(db);
            CreateTable.createTableUploadQueue(db);
            CreateTable.createTableKeyValue(db);
            CreateTable.createTableMonthlyReport(db);
            CreateTable.createTableItem(db);
            CreateTable.createTableExpenseTag(db);
            CreateTable.createTableNotification(db);
            CreateTable.createTableBillingAccount(db);
            CreateTable.createTableBillingHistory(db);
        }
    }

    public SQLiteDatabase getDb() {
        if (null == db) {
            Log.d(TAG, "db is NULL, re-initialized");
            db = dbInstance.getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
