package com.receiptofi.checkout.utils.db;

import com.receiptofi.checkout.db.DatabaseTable;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbReInitialize() {
        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Receipt.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.KeyValue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.ImageIndex.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.UploadQueue.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.MonthlyReport.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + DatabaseTable.Item.TABLE_NAME);


        /** Create tables. */
        RDH.createTableReceipts();
        RDH.createTableImageIndex();
        RDH.createTableKeyValue();
        RDH.createTableUploadQueue();
        RDH.createTableMonthlyReport();
        RDH.createTableItem();

        initializeDefaults();
    }

    public static void initializeDefaults() {
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_MAIL, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_AUTH, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.WIFI_SYNC, Boolean.toString(true));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, "0");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.SOCIAL_LOGIN, Boolean.toString(false));
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.LAST_FETCHED, "");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_DID, "");
    }
}
