package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.BillingAccountModel;
import com.receiptofi.checkout.model.BillingHistoryModel;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 4/19/15 7:32 PM
 */
public class BillingAccountUtils {
    private static final String TAG = BillingAccountUtils.class.getSimpleName();

    /**
     * Insert billing account in table.
     *
     * @param billingAccount
     */
    public static void insert(BillingAccountModel billingAccount) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.BillingAccount.ACCOUNT_BILLING_TYPE, billingAccount.getAccountBillingType());
        values.put(DatabaseTable.BillingAccount.BILLED_ACCOUNT, billingAccount.isBilledAccount());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.BillingAccount.TABLE_NAME,
                null,
                values
        );

        if (!billingAccount.getBillingHistories().isEmpty()) {
            for (BillingHistoryModel billingHistoryModel : billingAccount.getBillingHistories()) {
                BillingHistoryUtils.insert(billingHistoryModel);
            }
        }
    }

    private static BillingAccountModel getBillingAccount() {
        Log.d(TAG, "Fetching all billing history");
        BillingAccountModel billingAccountModel = null;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.BillingAccount.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );


            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    billingAccountModel = new BillingAccountModel(
                            cursor.getString(0),
                            cursor.getInt(1) == 1
                    );
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting billing history " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return billingAccountModel;
    }
}
