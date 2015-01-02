package com.receiptofi.checkout.dbutils;


import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.ReceiptDB;
import com.receiptofi.checkout.db.ReceiptofiDatabaseHandler;
import com.receiptofi.checkout.utils.AppUtils;

public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    /**
     * Delete all tables and create all tables with default settings.
     */
    public static void dbInitialize(){
        //Delete all tables
        Log.d(TAG, "Executing Drop Table : "+ReceiptDB.Receipt.TABLE_NAME);
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.Receipt.TABLE_NAME);
        Log.d(TAG, "Dropped Table : "+ReceiptDB.Receipt.TABLE_NAME);
        Log.d(TAG, "Executing Drop Table : "+ReceiptDB.KeyVal.TABLE_NAME);
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.KeyVal.TABLE_NAME);
        Log.d(TAG, "Dropped Table : "+ReceiptDB.KeyVal.TABLE_NAME);
        Log.d(TAG, "Executing Drop Table : "+ReceiptDB.ImageIndex.TABLE_NAME);
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.ImageIndex.TABLE_NAME);
        Log.d(TAG, "Dropped Table : "+ReceiptDB.ImageIndex.TABLE_NAME);
        Log.d(TAG, "Executing Drop Table : "+ReceiptDB.UploadQueue.TABLE_NAME);
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.UploadQueue.TABLE_NAME);
        Log.d(TAG, "Dropped Table : "+ReceiptDB.UploadQueue.TABLE_NAME);

        //Create all tables

        Log.d(TAG, "Executing Create Table : "+ReceiptDB.Receipt.TABLE_NAME);
        ReceiptofiApplication.RDH.createTableReceipts();
        Log.d(TAG, "Created Table : " + ReceiptDB.Receipt.TABLE_NAME);
        Log.d(TAG, "Executing Create Table : "+ReceiptDB.ImageIndex.TABLE_NAME);
        ReceiptofiApplication.RDH.createTableImageIndex();
        Log.d(TAG, "Created Table : " + ReceiptDB.ImageIndex.TABLE_NAME);
        Log.d(TAG, "Executing Create Table : "+ReceiptDB.KeyVal.TABLE_NAME);
        ReceiptofiApplication.RDH.createTableKeyValue();
        Log.d(TAG, "Created Table : " + ReceiptDB.KeyVal.TABLE_NAME);
        Log.d(TAG, "Executing Create Table : "+ReceiptDB.UploadQueue.TABLE_NAME);
        ReceiptofiApplication.RDH.createTableUploadQueue();
        Log.d(TAG, "Created Table : " + ReceiptDB.UploadQueue.TABLE_NAME);

        //Set Default for Wi-Fi
        KeyValueUtils.insertKeyValue(AppUtils.getHomePageContext(), KeyValueUtils.KEYS.WIFI_SYNC,"true");
    }
}
