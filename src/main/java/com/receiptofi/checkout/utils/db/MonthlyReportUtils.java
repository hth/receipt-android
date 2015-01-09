package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;

import java.util.LinkedList;
import java.util.List;

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
        RDH.createTableMonthlyReport();
    }

    private static void groupByMonthlyReceiptStat() {
        Log.d(TAG, "Starting to calculate Monthly Facts");

        Cursor cursor = RDH.getReadableDatabase().rawQuery(
                "select " +
                        "SUBSTR(date, 6, 2) mon," +
                        "SUBSTR(date, 1, 4) yr, " +
                        "total(total) total, " +
                        "count(*) count " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " group by mon, yr", null);


        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getDouble(2),
                        cursor.getInt(3));

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

    public static String fetchMonthlyTotal(String year, String month) {
        Log.d(TAG, "Starting Fetch Monthly Total");
        Cursor cursor = RDH.getReadableDatabase().rawQuery(
                " select " +
                        "total from " + MonthlyReport.TABLE_NAME + " " +
                        "where year = '" + year + "' " +
                        "and month = '" + month + "'", null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        }
        return "0.0";
    }

    private static List<ReceiptItemModel> getItems(String receiptId) {
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Item.TABLE_NAME,
                null,
                DatabaseTable.Item.RECEIPTID + " = ?",
                new String[]{receiptId},
                null,
                null,
                DatabaseTable.Item.SEQUENCE + " desc"
        );

        List<ReceiptItemModel> list = new LinkedList<>();
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ReceiptItemModel receiptItemModel = new ReceiptItemModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );

                list.add(receiptItemModel);
            }
        }

        return list;
    }

    private static List<ReceiptModel> fetchReceipts(String year, String month) {
        Log.d(TAG, "Fetching Receipt Data for a given Month & Year from Receipt Table");

        List<ReceiptModel> list = new LinkedList<>();
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                "SUBSTR(date, 6, 2) = ? and SUBSTR(date, 1, 4) = ? ",
                new String[]{month, year},
                null,
                null,
                DatabaseTable.Receipt.DATE + " desc"
        );

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ReceiptModel receiptModel = new ReceiptModel();
                receiptModel.setBizName(cursor.getString(0));
                receiptModel.setAddress(cursor.getString(1));
                receiptModel.setPhone(cursor.getString(2));
                receiptModel.setDate(cursor.getString(3));
                receiptModel.setExpenseReport(cursor.getString(4));
                receiptModel.setBlobIds(cursor.getString(5));
                receiptModel.setId(cursor.getString(6));
                receiptModel.setNotes(cursor.getString(7));
                receiptModel.setPtax(cursor.getDouble(8));
                receiptModel.setRid(cursor.getString(9));
                receiptModel.setTotal(cursor.getDouble(10));

                receiptModel.setReceiptItems(getItems(receiptModel.getId()));
                list.add(receiptModel);
            }
        }
        return list;
    }

    public static ReceiptGroup fetchMonthly() {
        Log.d(TAG, "Fetching Receipt Monthly Fact Data from MonthlyReport Table");

        ReceiptGroup receiptGroup = new ReceiptGroup();
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.MonthlyReport.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MonthlyReport.YEAR + ", " + MonthlyReport.MONTH + " desc"
        );

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String month = cursor.getString(0);
                String year = cursor.getString(1);
                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        month,
                        year,
                        Double.parseDouble(cursor.getString(2)),
                        Integer.parseInt(cursor.getString(3))
                );
                receiptGroup.addReceiptGroupHeader(receiptGroupHeader);
                receiptGroup.addReceiptGroup(fetchReceipts(year, month));
            }
        }
        return receiptGroup;
    }
}
