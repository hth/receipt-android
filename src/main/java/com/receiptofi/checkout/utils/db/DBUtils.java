package com.receiptofi.checkout.utils.db;

import com.receiptofi.checkout.db.ReceiptDB;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbReInitialize() {
        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.Receipt.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.KeyVal.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.ImageIndex.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.UploadQueue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.MonthlyReport.TABLE_NAME);

        /** Create tables. */
        RDH.createTableReceipts();
        RDH.createTableImageIndex();
        RDH.createTableKeyValue();
        RDH.createTableUploadQueue();
        RDH.createTableMonthlyReport();

        initializeDefaults();
    }

    public static void initializeDefaults() {
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_MAIL, "");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_AUTH, "");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.WIFI_SYNC, Boolean.toString(true));
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, "0");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.SOCIAL_LOGIN, Boolean.toString(false));
    }
}
