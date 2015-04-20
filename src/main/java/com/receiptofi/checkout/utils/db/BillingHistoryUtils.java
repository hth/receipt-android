package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
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

    /**
     * Insert billing history in table.
     *
     * @param billingHistory
     */
    public static void insert(BillingHistoryModel billingHistory) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.BillingHistory.ID, billingHistory.getId());
        values.put(DatabaseTable.BillingHistory.BILLED_MONTH, billingHistory.getBilledForMonth());
        values.put(DatabaseTable.BillingHistory.BILLED_STATUS, billingHistory.getBilledStatus());
        values.put(DatabaseTable.BillingHistory.ACCOUNT_BILLING_TYPE, billingHistory.getAccountBillingType());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.BillingHistory.TABLE_NAME,
                null,
                values
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
                    DatabaseTable.BillingHistory.BILLED_MONTH
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    BillingHistoryModel billingHistoryModel = new BillingHistoryModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3)
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
