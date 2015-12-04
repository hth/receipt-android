package com.receiptofi.receiptapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.receiptofi.receiptapp.utils.db.KeyValueUtils;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHandler.class.getSimpleName();

    private static final int DB_VERSION = 2;
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
            CreateTable.createTableReceiptSplit(db);
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
        Log.d(TAG, "DatabaseHandler onUpgrade");
        for (Patch patch : PATCHES) {
            patch.apply(db);
        }
    }

    private static final Patch[] PATCHES = new Patch[]{
            new Patch(1, 2, "1.0.51") {
                public void apply(SQLiteDatabase db) {
                    CreateTable.createTableReceiptSplit(db);

                    db.execSQL(
                            "ALTER TABLE " +
                                    DatabaseTable.Receipt.TABLE_NAME +
                                    " RENAME TO " +
                                    DatabaseTable.Receipt.TABLE_NAME + "_old;");

                    CreateTable.createTableReceipts(db);
                    db.execSQL(
                            "INSERT INTO " + DatabaseTable.Receipt.TABLE_NAME +
                                    "(" +
                                    DatabaseTable.Receipt.BIZ_NAME + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_ADDRESS + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_PHONE + ", " +
                                    DatabaseTable.Receipt.RECEIPT_DATE + ", " +
                                    DatabaseTable.Receipt.EXPENSE_REPORT + ", " +
                                    DatabaseTable.Receipt.BLOB_IDS + ", " +
                                    DatabaseTable.Receipt.ID + ", " +
                                    DatabaseTable.Receipt.NOTES + ", " +
                                    DatabaseTable.Receipt.PTAX + ", " +
                                    DatabaseTable.Receipt.RID + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.BILL_STATUS + ", " +
                                    DatabaseTable.Receipt.EXPENSE_TAG_ID + ", " +
                                    DatabaseTable.Receipt.SPLIT_COUNT + ", " +
                                    DatabaseTable.Receipt.SPLIT_TOTAL + ", " +
                                    DatabaseTable.Receipt.SPLIT_TAX + ", " +
                                    DatabaseTable.Receipt.ACTIVE + ", " +
                                    DatabaseTable.Receipt.DELETED +
                                    ") SELECT " +
                                    DatabaseTable.Receipt.BIZ_NAME + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_ADDRESS + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_PHONE + ", " +
                                    DatabaseTable.Receipt.RECEIPT_DATE + ", " +
                                    DatabaseTable.Receipt.EXPENSE_REPORT + ", " +
                                    DatabaseTable.Receipt.BLOB_IDS + ", " +
                                    DatabaseTable.Receipt.ID + ", " +
                                    DatabaseTable.Receipt.NOTES + ", " +
                                    DatabaseTable.Receipt.PTAX + ", " +
                                    DatabaseTable.Receipt.RID + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.BILL_STATUS + ", " +
                                    DatabaseTable.Receipt.EXPENSE_TAG_ID + ", " +
                                    1 + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.ACTIVE + ", " +
                                    DatabaseTable.Receipt.DELETED +
                                    " FROM " + DatabaseTable.Receipt.TABLE_NAME + "_old;");
                    db.execSQL("DROP TABLE " + DatabaseTable.Receipt.TABLE_NAME + "_old;");
                                        
                    db.execSQL("UPDATE " + DatabaseTable.KeyValue.TABLE_NAME + " SET " +
                            DatabaseTable.KeyValue.VALUE + " = '" + Boolean.toString(false) + "' " +
                            "WHERE " + DatabaseTable.KeyValue.KEY + " = '" + KeyValueUtils.KEYS.WIFI_SYNC + "';");
                }
            },
            new Patch(2, 3, "1.1.3") {
                public void apply(SQLiteDatabase db) {
                    db.execSQL(
                            "ALTER TABLE " +
                                    DatabaseTable.Receipt.TABLE_NAME +
                                    " RENAME TO " +
                                    DatabaseTable.Receipt.TABLE_NAME + "_old;");

                    CreateTable.createTableReceipts(db);
                    db.execSQL(
                            "INSERT INTO " + DatabaseTable.Receipt.TABLE_NAME +
                                    "(" +
                                    DatabaseTable.Receipt.BIZ_NAME + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_ADDRESS + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_PHONE + ", " +
                                    DatabaseTable.Receipt.LAT + ", " +
                                    DatabaseTable.Receipt.LNG + ", " +
                                    DatabaseTable.Receipt.TYPE + ", " +
                                    DatabaseTable.Receipt.RATING + ", " +
                                    DatabaseTable.Receipt.RECEIPT_DATE + ", " +
                                    DatabaseTable.Receipt.EXPENSE_REPORT + ", " +
                                    DatabaseTable.Receipt.BLOB_IDS + ", " +
                                    DatabaseTable.Receipt.ID + ", " +
                                    DatabaseTable.Receipt.NOTES + ", " +
                                    DatabaseTable.Receipt.PTAX + ", " +
                                    DatabaseTable.Receipt.RID + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.BILL_STATUS + ", " +
                                    DatabaseTable.Receipt.EXPENSE_TAG_ID + ", " +
                                    DatabaseTable.Receipt.REFER_RECEIPT_ID + ", " +
                                    DatabaseTable.Receipt.SPLIT_COUNT + ", " +
                                    DatabaseTable.Receipt.SPLIT_TOTAL + ", " +
                                    DatabaseTable.Receipt.SPLIT_TAX + ", " +
                                    DatabaseTable.Receipt.ACTIVE + ", " +
                                    DatabaseTable.Receipt.DELETED +
                                    ") SELECT " +
                                    DatabaseTable.Receipt.BIZ_NAME + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_ADDRESS + ", " +
                                    DatabaseTable.Receipt.BIZ_STORE_PHONE + ", " +
                                    0 + ", " +
                                    0 + ", " +
                                    "" + ", " +
                                    0 + ", " +
                                    DatabaseTable.Receipt.RECEIPT_DATE + ", " +
                                    DatabaseTable.Receipt.EXPENSE_REPORT + ", " +
                                    DatabaseTable.Receipt.BLOB_IDS + ", " +
                                    DatabaseTable.Receipt.ID + ", " +
                                    DatabaseTable.Receipt.NOTES + ", " +
                                    DatabaseTable.Receipt.PTAX + ", " +
                                    DatabaseTable.Receipt.RID + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.BILL_STATUS + ", " +
                                    DatabaseTable.Receipt.EXPENSE_TAG_ID + ", " +
                                    DatabaseTable.Receipt.REFER_RECEIPT_ID + ", " +
                                    DatabaseTable.Receipt.SPLIT_COUNT + ", " +
                                    DatabaseTable.Receipt.SPLIT_TOTAL + ", " +
                                    DatabaseTable.Receipt.TOTAL + ", " +
                                    DatabaseTable.Receipt.TAX + ", " +
                                    DatabaseTable.Receipt.ACTIVE + ", " +
                                    DatabaseTable.Receipt.DELETED +
                                    " FROM " + DatabaseTable.Receipt.TABLE_NAME + "_old;");
                    db.execSQL("DROP TABLE " + DatabaseTable.Receipt.TABLE_NAME + "_old;");
                }
            }
    };
}
