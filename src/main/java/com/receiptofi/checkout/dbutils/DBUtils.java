package com.receiptofi.checkout.dbutils;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.ReceiptDB;

/**
 * Created by ptholia on 1/1/15.
 */
public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and create all tables with default settings.
     */
    public static void dbInitialize() {
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.Receipt.TABLE_NAME);

        //TODO(pth) Create table

        //TODO (pth) Set Default for Wi-Fi
    }
}
