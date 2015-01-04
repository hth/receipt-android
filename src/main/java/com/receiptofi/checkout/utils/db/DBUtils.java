package com.receiptofi.checkout.utils.db;

import com.receiptofi.checkout.db.DatabaseTable;
import java.util.UUID;

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
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_MAIL, "");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_AUTH, "");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.WIFI_SYNC, Boolean.toString(true));
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, "0");
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.SOCIAL_LOGIN, Boolean.toString(false));
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_DID, UUID.randomUUID().toString());
    }
}
