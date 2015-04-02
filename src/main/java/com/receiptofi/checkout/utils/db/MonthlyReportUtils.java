package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.db.CreateTable;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptGroupObservable;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.db.DatabaseTable.MonthlyReport;

public class MonthlyReportUtils {

    private static final String TAG = MonthlyReportUtils.class.getSimpleName();

    public static void computeMonthlyReceiptReport() {
        Log.d(TAG, "Compute monthly receipt report");
        dropAndCreateTableMonthlyReport();
        groupByMonthlyReceiptStat();
    }

    private static void dropAndCreateTableMonthlyReport() {
        Log.d(TAG, "Drop monthly receipt report");
        RDH.getWritableDatabase().execSQL("Drop table if exists " + MonthlyReport.TABLE_NAME);
        Log.d(TAG, "Create monthly receipt report");
        CreateTable.createTableMonthlyReport(RDH.getDb());
    }

    private static void groupByMonthlyReceiptStat() {
        Log.d(TAG, "Starting to calculate Monthly Facts");

        Cursor cursor = RDH.getReadableDatabase().rawQuery(
                "select " +
                        "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 6, 2) mon," +
                        "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 1, 4) yr, " +
                        "total(total) total, " +
                        "count(*) count " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " group by mon, yr", null);


        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3));

                if (insert(receiptGroupHeader)) {
                    Log.d(TAG, "Monthly Record Inserted Successfully " + receiptGroupHeader);
                } else {
                    Log.d(TAG, "Monthly Record Insert Failed " + receiptGroupHeader);
                }
            }
        }
    }

    /**
     * Inserts monthly receipt stats.
     *
     * @param receiptGroupHeader
     * @return
     */
    private static boolean insert(ReceiptGroupHeader receiptGroupHeader) {
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

    public static String fetchMonthlyTotal(String year, String month) {
        Log.d(TAG, "Starting Fetch Monthly Total");
        Cursor cursor = RDH.getReadableDatabase().rawQuery(
                " select " +
                        "total from " + MonthlyReport.TABLE_NAME + " " +
                        "where " + MonthlyReport.YEAR + " = '" + year + "' " +
                        "and " + MonthlyReport.MONTH + " = '" + month + "'", null);

        //TODO try changing to while(cursor.moveToNext()) from if
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        return "0.0";
    }

    public static ReceiptGroup fetchMonthly() {
        Log.d(TAG, "All receipt grouped MonthlyReport");

        ReceiptGroup receiptGroup = ReceiptGroup.getInstance();
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.MonthlyReport.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MonthlyReport.YEAR + " desc, " + MonthlyReport.MONTH + " desc"
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String month = cursor.getString(0);
                String year = cursor.getString(1);
                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        month,
                        year,
                        Double.parseDouble(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3))
                );
                receiptGroup.addReceiptGroupHeader(receiptGroupHeader);
                receiptGroup.addReceiptGroup(ReceiptUtils.fetchReceipts(year, month));
            }
        }
        ReceiptGroupObservable.setMonthlyReceiptGroup(receiptGroup);
        return receiptGroup;
    }
}
