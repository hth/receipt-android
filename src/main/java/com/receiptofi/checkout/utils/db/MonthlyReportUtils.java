package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
                        "count(*) count, " +
                        "total(total) total " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " group by mon, yr", null);


        if (receiptsMonthlyCursor != null && receiptsMonthlyCursor.getCount() > 0) {
            for (receiptsMonthlyCursor.moveToFirst(); !receiptsMonthlyCursor.isAfterLast(); receiptsMonthlyCursor.moveToNext()) {

                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        receiptsMonthlyCursor.getString(0),
                        receiptsMonthlyCursor.getString(1),
                        receiptsMonthlyCursor.getDouble(3),
                        receiptsMonthlyCursor.getInt(2));

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

    private static List<ReceiptModel> fetchReceipts(String year, String month){

        Log.d(TAG, "Fetching Receipt Data for a given Month & Year from Receipt Table");

        List<ReceiptModel> list = new LinkedList<ReceiptModel>();
        ReceiptModel receiptModel = new ReceiptModel();

        Cursor receiptCursor = RDH.getReadableDatabase().rawQuery(
                " select * from " + DatabaseTable.Receipt.TABLE_NAME +
                " where SUBSTR(date, 6, 2)  = '"+month+"' and SUBSTR(date, 1, 4) = '"+year+"' +" +
                " order by date desc ",null);

        if (receiptCursor != null && receiptCursor.getCount() > 0) {
            for (receiptCursor.moveToFirst(); !receiptCursor.isAfterLast(); receiptCursor.moveToNext()) {

                receiptModel.setBizName(receiptCursor.getString(0));
                receiptModel.setAddress(receiptCursor.getString(1));
                receiptModel.setPhone(receiptCursor.getString(2));
                receiptModel.setDate(receiptCursor.getString(3));

                list.add(receiptModel);
            }
        }
        return list;
    }

    private static ReceiptGroup fetchMonthly(){

        Log.d(TAG, "Fetching Receipt Monthly Fact Data from MonthlyReport Table");

        ReceiptGroup rg = new ReceiptGroup();

        //For Receipt List Headers
        ReceiptGroupHeader receiptGroupHeader;

        Cursor monthlyCursor = RDH.getReadableDatabase().rawQuery(
                " select * from " + MonthlyReport.TABLE_NAME +
                        " order by year, mon desc", null);

        if  (monthlyCursor != null && monthlyCursor.getCount() > 0) {
            for (monthlyCursor.moveToFirst(); !monthlyCursor.isAfterLast(); monthlyCursor.moveToNext()) {

                String month = monthlyCursor.getString(0);
                String year = monthlyCursor.getString(1);
                receiptGroupHeader = new ReceiptGroupHeader(month, year, Double.parseDouble(monthlyCursor.getString(3)), Integer.parseInt(monthlyCursor.getString(3)));
                rg.addReceiptGroupHeaders(receiptGroupHeader);

                //Get Receipts for current month
                rg.addReceiptGroup(fetchReceipts(year, month));
            }
        }
        return rg;

    }


}
