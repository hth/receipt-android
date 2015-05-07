package com.receiptofi.checkout.utils.db;

import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.BillingHistoryModel;

import java.util.LinkedList;
import java.util.List;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 4/19/15 7:32 PM
 */
public class BillingHistoryUtils {
    private static final String TAG = BillingHistoryUtils.class.getSimpleName();

    private BillingHistoryUtils() {
    }

    /**
     * Insert billing history in table.
     *
     * @param billingHistory
     */
    public static void insertOrReplace(BillingHistoryModel billingHistory) {
        ReceiptofiApplication.RDH.getWritableDatabase().execSQL(
                "INSERT OR REPLACE INTO " + DatabaseTable.BillingHistory.TABLE_NAME + " (" +
                        DatabaseTable.BillingHistory.ID + ", " +
                        DatabaseTable.BillingHistory.BILLED_MONTH + ", " +
                        DatabaseTable.BillingHistory.BILLED_STATUS + ", " +
                        DatabaseTable.BillingHistory.ACCOUNT_BILLING_TYPE + ", " +
                        DatabaseTable.BillingHistory.BILLED_DATE +
                        ") VALUES (?, ?, ?, ?, ?)",
                new String[]{
                        billingHistory.getId(),
                        billingHistory.getBilledForMonth(),
                        billingHistory.getBilledStatus(),
                        billingHistory.getAccountBillingType(),
                        billingHistory.getBilledDate()
                }
        );
    }

    public static List<BillingHistoryModel> getAll() {
        Log.d(TAG, "Fetching all billing history");
        List<BillingHistoryModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.BillingHistory.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseTable.BillingHistory.BILLED_MONTH + " desc"
            );

            if (null != cursor && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    BillingHistoryModel billingHistoryModel = new BillingHistoryModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4)
                    );

                    list.add(billingHistoryModel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting billing history " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }
}
