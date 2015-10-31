package com.receiptofi.receiptapp.utils.db;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;

import com.receiptofi.receiptapp.db.CreateTable;
import com.receiptofi.receiptapp.db.DatabaseTable;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    private DBUtils() {
    }

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbReInitialize() {
        dbReInitializeNonKeyValues();
        dbReInitializeKeyValues();

        initializeDefaults();
        initializeAuthDefaults();
    }

    public static void dbReInitializeNonKeyValues() {
        Log.d(TAG, "Initialize database");

        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Profile.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Receipt.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ReceiptSplit.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ImageIndex.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.UploadQueue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.MonthlyReport.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Item.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ExpenseTag.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Notification.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.BillingAccount.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.BillingHistory.TABLE_NAME);

        /** Create tables. */
        CreateTable.createTableProfile(RDH.getDb());
        CreateTable.createTableReceipts(RDH.getDb());
        CreateTable.createTableReceiptSplit(RDH.getDb());
        CreateTable.createTableImageIndex(RDH.getDb());
        CreateTable.createTableUploadQueue(RDH.getDb());
        CreateTable.createTableMonthlyReport(RDH.getDb());
        CreateTable.createTableItem(RDH.getDb());
        CreateTable.createTableExpenseTag(RDH.getDb());
        CreateTable.createTableNotification(RDH.getDb());
        CreateTable.createTableBillingAccount(RDH.getDb());
        CreateTable.createTableBillingHistory(RDH.getDb());
    }

    private  static void dbReInitializeKeyValues() {
        Log.d(TAG, "Initialize table " + DatabaseTable.KeyValue.TABLE_NAME);

        /** Delete table. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.KeyValue.TABLE_NAME);

        /** Create table. */
        CreateTable.createTableKeyValue(RDH.getDb());
    }

    /**
     * Invoked in three different scenarios.
     * Once called during onCreate() when app is installed
     * And when X-R-MAIL does not exists
     * And when new user logs in.
     */
    public static void initializeDefaults() {
        Log.d(TAG, "Initialize key value defaults");

        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.WIFI_SYNC, Boolean.toString(true));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, "0");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.SOCIAL_LOGIN, Boolean.toString(false));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.LAST_FETCHED, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.GET_ALL_COMPLETE, Boolean.toString(false));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.LATEST_APK, "");
    }

    private static void initializeAuthDefaults() {
        Log.d(TAG, "Initialize key value auth defaults");

        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_MAIL, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_AUTH, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_DID, "");
    }

    /**
     * Counts tables in SQLite.
     *
     * @return
     */
    public static int countTables() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "SELECT count(*) FROM sqlite_master WHERE " +
                            "type = 'table' AND " +
                            "name != 'android_metadata' AND " +
                            "name != 'sqlite_sequence'",
                    null);

            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting tables " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        Log.d(TAG, "Number of table count in db " + count);
        return count;
    }

    public static void clearDB(String tableName) {
        RDH.getReadableDatabase().delete(
                tableName,
                null,
                null
        );
    }

    /**
     * Escape business name like "St John's Bar & Grill" and replaces string with "'St John''s Bar & Grill'"
     *
     * @param value
     * @return
     */
    public static String sqlEscapeString(String value) {
        return DatabaseUtils.sqlEscapeString(value);
    }
}
