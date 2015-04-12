package com.receiptofi.checkout.utils.db;

import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.db.CreateTable;
import com.receiptofi.checkout.db.DatabaseTable;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbReInitialize() {
        Log.d(TAG, "Initialize database");

        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Receipt.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.KeyValue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ImageIndex.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.UploadQueue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.MonthlyReport.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Item.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ExpenseTag.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Notification.TABLE_NAME);

        /** Create tables. */
        CreateTable.createTableReceipts(RDH.getDb());
        CreateTable.createTableImageIndex(RDH.getDb());
        CreateTable.createTableKeyValue(RDH.getDb());
        CreateTable.createTableUploadQueue(RDH.getDb());
        CreateTable.createTableMonthlyReport(RDH.getDb());
        CreateTable.createTableItem(RDH.getDb());
        CreateTable.createTableExpenseTag(RDH.getDb());
        CreateTable.createTableNotification(RDH.getDb());

        initializeDefaults();
    }

    /**
     * Invoked in three different scenarios.
     * Once called during onCreate() when app is installed
     * And when X-R-MAIL does not exists
     * And when new user logs in.
     */
    public static void initializeDefaults() {
        Log.d(TAG, "Initialize defaults");

        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_MAIL, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_AUTH, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.WIFI_SYNC, Boolean.toString(true));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, "0");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.SOCIAL_LOGIN, Boolean.toString(false));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.LAST_FETCHED, "");
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
}
