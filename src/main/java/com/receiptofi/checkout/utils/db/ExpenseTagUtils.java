package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ExpenseTagModel;;

import java.util.List;

/**
 * User: hitender
 * Date: 1/24/15 1:03 AM
 */
public class ExpenseTagUtils {

    private static final String TAG = ExpenseTagUtils.class.getSimpleName();

    public static void insertExpenseTag(List<ExpenseTagModel> expensesTags) {
        for(ExpenseTagModel expenseTag : expensesTags) {
            insertExpenseTag(expenseTag);
        }
    }

    /**
     * Insert item in table.
     *
     * @param expenseTag
     */
    private static void insertExpenseTag(ExpenseTagModel expenseTag) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ExpenseTag.ID, expenseTag.getId());
        values.put(DatabaseTable.ExpenseTag.TAG, expenseTag.getTag());
        values.put(DatabaseTable.ExpenseTag.COLOR, expenseTag.getColor());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ExpenseTag.TABLE_NAME,
                null,
                values
        );
    }
}
