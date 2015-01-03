package com.receiptofi.checkout.utils.db;


import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.ReceiptDB;

public class MonthlyReportUtils {

    private static final String TAG = MonthlyReportUtils.class.getSimpleName();

    public static boolean insertMonthlyFacts(Context context, String month, String year,
                                          String total, String count){

        Log.d(TAG, "Setting Data for MonthlyReport Table");

        ContentValues values = new ContentValues();
        values.put(ReceiptDB.MonthlyReport.MONTH, month);
        values.put(ReceiptDB.MonthlyReport.YEAR, year);
        values.put(ReceiptDB.MonthlyReport.TOTAL_AMT, total);
        values.put(ReceiptDB.MonthlyReport.RECEIPT_COUNT, count);

        Log.d(TAG, "Data values to be Inserted - Month : " + month + "Year : " + year + "Total Amount : " + total + "Receipt Count : " + count);

        return ReceiptofiApplication.RDH.getWritableDatabase().insert(ReceiptDB.MonthlyReport.TABLE_NAME,
                                                               null,
                                                               values) > 0;

    }

    public static void dropAndCreateTableMonthlyReport(Context context, String tableName){

        Log.d(TAG, "Dropping Table - MonthlyReport");

        ReceiptofiApplication.RDH.getWritableDatabase().execSQL("Drop table if exists " + ReceiptDB.MonthlyReport.TABLE_NAME);

        Log.d(TAG, "Dropped Table - MonthlyReport, now Creating it ");

        ReceiptofiApplication.RDH.createTableMonthlyReport();

        Log.d(TAG, "Created Table - MonthlyReport");

    }
}
