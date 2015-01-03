package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.db.ReceiptDB.MonthlyReport;

public class MonthlyReportUtils {

    private static final String TAG = MonthlyReportUtils.class.getSimpleName();

    public static boolean insertMonthlyFacts(
            String month,
            String year,
            String total,
            String count
    ) {
        Log.d(TAG, "Setting Data for MonthlyReport Table");

        ContentValues values = new ContentValues();
        values.put(MonthlyReport.MONTH, month);
        values.put(MonthlyReport.YEAR, year);
        values.put(MonthlyReport.TOTAL_AMT, total);
        values.put(MonthlyReport.RECEIPT_COUNT, count);

        Log.d(TAG, "Data values to be Inserted - Month : " + month + "Year : " + year + "Total Amount : " + total + "Receipt Count : " + count);

        return RDH.getWritableDatabase().insert(
                MonthlyReport.TABLE_NAME,
                null,
                values
        ) > 0;

    }

    public static void dropAndCreateTableMonthlyReport() {
        RDH.getWritableDatabase().execSQL("Drop table if exists " + MonthlyReport.TABLE_NAME);
        RDH.createTableMonthlyReport();
    }
}
