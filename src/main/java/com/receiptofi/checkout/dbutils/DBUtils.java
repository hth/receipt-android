package com.receiptofi.checkout.dbutils;


import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.ReceiptDB;

/**
 * Created by ptholia on 1/1/15.
 */
public class DBUtils {

    private static final String TAG = DBUtils.class.getSimpleName();

    public static void dbInitialize(){
        //Delete all tables
       Log.d(TAG, "Executing Drop Table : "+ReceiptDB.Receipt.TABLE_NAME);
       ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.Receipt.TABLE_NAME);
       Log.d(TAG, "Dropped Table : "+ReceiptDB.Receipt.TABLE_NAME);
        //Create all tables

        //Set Default for Wi-Fi



    }
}
