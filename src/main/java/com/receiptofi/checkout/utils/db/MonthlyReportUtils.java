package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.db.DatabaseTable.MonthlyReport;

public class MonthlyReportUtils {

    private static final String TAG = MonthlyReportUtils.class.getSimpleName();

    private static boolean insertMonthlyFacts(
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

    public static void calculateMonthlyFacts(){
        Log.d(TAG,"Starting to calculate Monthly Facts");

        Cursor receiptsMonthlyCursor = ReceiptofiApplication.RDH.getReadableDatabase().rawQuery("select count(*) num_dates,total(total) total, substr(date, 1, 4) yr,substr(date, 6, 2) mon from Receipt group by yr,mon ",null);
        if(receiptsMonthlyCursor != null)
        {
            receiptsMonthlyCursor.moveToFirst();
        }
        while(receiptsMonthlyCursor.isLast()) {
            String receiptCount = receiptsMonthlyCursor.getString(0);
            String totalAmt = receiptsMonthlyCursor.getString(1);
            String year = receiptsMonthlyCursor.getString(2);
            String month = receiptsMonthlyCursor.getString(3);

            Log.d(TAG,"Before calling insertMonthlyFacts, values are Receipt Count : "+receiptCount+" Total Amt : "+totalAmt+" Year : "+year+" Month : "+month);
            boolean recInserted = insertMonthlyFacts(month,year,totalAmt,receiptCount);

            if (recInserted)
                Log.d(TAG,"Record Inserted Successfully");
            else Log.d(TAG,"Record Insert Failed - Year and Month Rec : "+year+" - "+month);
        }

    }

}
