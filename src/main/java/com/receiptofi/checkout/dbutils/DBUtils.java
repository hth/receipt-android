package com.receiptofi.checkout.dbutils;

import android.util.Log;

import com.receiptofi.checkout.db.ReceiptDB;
import com.receiptofi.checkout.utils.AppUtils;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.dbutils.KeyValueUtils.KEYS;
import static com.receiptofi.checkout.dbutils.KeyValueUtils.*;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and re-create all tables with default settings.
     */
    public static void dbInitialize() {
        /** Delete all tables. */
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.Receipt.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.KeyVal.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.ImageIndex.TABLE_NAME);
        RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.UploadQueue.TABLE_NAME);

        /** Create tables. */
        RDH.createTableReceipts();
        RDH.createTableImageIndex();
        RDH.createTableKeyValue();
        RDH.createTableUploadQueue();

        initializeDefaults();
    }

    public static void initializeDefaults() {
        insertKeyValue(AppUtils.getHomePageContext(), KEYS.XR_MAIL, "");
        insertKeyValue(AppUtils.getHomePageContext(), KEYS.XR_AUTH, "");
        insertKeyValue(AppUtils.getHomePageContext(), KEYS.WIFI_SYNC, Boolean.toString(true));
        insertKeyValue(AppUtils.getHomePageContext(), KEYS.UNPROCESSED_DOCUMENT, "0");
        insertKeyValue(AppUtils.getHomePageContext(), KEYS.SOCIAL_LOGIN, Boolean.toString(false));
    }
}
