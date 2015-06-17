package com.receiptofi.checkout.utils.db;

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

    private BillingAccountUtils() {
    }

    /**
     * Insert billing account in table.
     *
     * @param billingAccount
     */
    public static void insertOrReplace(BillingAccountModel billingAccount) {
        DBUtils.clearDB(DatabaseTable.BillingAccount.TABLE_NAME);

        ReceiptofiApplication.RDH.getWritableDatabase().execSQL(
                "INSERT OR REPLACE INTO " + DatabaseTable.BillingAccount.TABLE_NAME + " (" +
                        DatabaseTable.BillingAccount.ACCOUNT_BILLING_TYPE +
                        ") VALUES ('" +
                        billingAccount.getAccountBillingType() + "')");

        if (!billingAccount.getBillingHistories().isEmpty()) {
            for (BillingHistoryModel billingHistoryModel : billingAccount.getBillingHistories()) {
                BillingHistoryUtils.insertOrReplace(billingHistoryModel);
            }
        }
    }

    public static BillingAccountModel getBillingAccount() {
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

            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToNext();
                
                billingAccountModel = new BillingAccountModel(cursor.getString(0));
                if (BillingHistoryUtils.getAll() != null) {
                    billingAccountModel.setBillingHistories(BillingHistoryUtils.getAll());
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
