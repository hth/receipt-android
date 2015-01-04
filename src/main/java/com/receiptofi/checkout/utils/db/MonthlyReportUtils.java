package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptGroupHeader;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.db.DatabaseTable.MonthlyReport;

public class MonthlyReportUtils {

    private static final String TAG = MonthlyReportUtils.class.getSimpleName();

    public static void computeMonthlyReceiptReport() {
        dropAndCreateTableMonthlyReport();
        groupByMonthlyReceiptStat();
    }

    private static void dropAndCreateTableMonthlyReport() {
        RDH.getWritableDatabase().execSQL("Drop table if exists " + MonthlyReport.TABLE_NAME);
        RDH.createTableMonthlyReport();
    }

    private static void groupByMonthlyReceiptStat() {
        Log.d(TAG, "Starting to calculate Monthly Facts");

        Cursor receiptsMonthlyCursor = RDH.getReadableDatabase().rawQuery(
                "select " +
                        "SUBSTR(date, 6, 2) mon," +
                        "SUBSTR(date, 1, 4) yr, " +
                        "total(total) total " +
                        "count(*) count, " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " group by mon, yr", null);


        if (receiptsMonthlyCursor != null && receiptsMonthlyCursor.getCount() > 0) {
            for (receiptsMonthlyCursor.moveToFirst(); !receiptsMonthlyCursor.isAfterLast(); receiptsMonthlyCursor.moveToNext()) {

                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        receiptsMonthlyCursor.getString(0),
                        receiptsMonthlyCursor.getString(1),
                        receiptsMonthlyCursor.getDouble(2),
                        receiptsMonthlyCursor.getInt(3));

                if (insertMonthlyReceiptStat(receiptGroupHeader)) {
                    Log.d(TAG, "Monthly Record Inserted Successfully " + receiptGroupHeader);
                } else {
                    Log.d(TAG, "Monthly Record Insert Failed " + receiptGroupHeader);
                }
            }
        }
    }

    private static boolean insertMonthlyReceiptStat(ReceiptGroupHeader receiptGroupHeader) {
        Log.d(TAG, "Setting Data for MonthlyReport Table");

        ContentValues values = new ContentValues();
        values.put(MonthlyReport.MONTH, receiptGroupHeader.getMonth());
        values.put(MonthlyReport.YEAR, receiptGroupHeader.getYear());
        values.put(MonthlyReport.TOTAL, receiptGroupHeader.getTotal());
        values.put(MonthlyReport.COUNT, receiptGroupHeader.getCount());

        return RDH.getWritableDatabase().insert(
                MonthlyReport.TABLE_NAME,
                null,
                values
        ) > 0;
    }

    public static String fetchMonthlyTotal(String year, String month){
        String monthlyTotal = null;
        Log.d(TAG,"Starting Fetch Monthly Total");
        Cursor monthlyTotalCursor = RDH.getReadableDatabase().rawQuery(
                                    " select total from " + MonthlyReport.TABLE_NAME +
                                    " where year = '"+year+"' and month = '"+month+"'",null);

        if (monthlyTotalCursor != null && monthlyTotalCursor.getCount() > 0)
            for (monthlyTotalCursor.moveToFirst(); !monthlyTotalCursor.isAfterLast(); monthlyTotalCursor.moveToNext()) {
                return monthlyTotal = monthlyTotalCursor.getString(0);
            }
                return "0.0";
    }
}
