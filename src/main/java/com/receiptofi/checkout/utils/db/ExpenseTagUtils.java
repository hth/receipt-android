package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ExpenseTagModel;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 1/24/15 1:03 AM
 */
public class ExpenseTagUtils {

    private static final String TAG = ExpenseTagUtils.class.getSimpleName();
    private static Map<String, ExpenseTagModel> expenseTagModels = new LinkedHashMap<>();

    /**
     * Expense Tag is static list available across the app. Anytime expense tag is added, deleted, updated, then
     * set new values to expenseTagModels.
     *
     * @return
     */
    public static synchronized Map<String, ExpenseTagModel> getExpenseTagModels() {
        if (expenseTagModels.isEmpty()) {
            populateExpenseTagModelMap();
        }
        return expenseTagModels;
    }

    public static void insert(List<ExpenseTagModel> expensesTags) {
        deleteAll();

        for (ExpenseTagModel expenseTag : expensesTags) {
            insert(expenseTag);
        }

        populateExpenseTagModelMap();
    }

    public static void deleteAll() {
        DBUtils.clearDB(DatabaseTable.ExpenseTag.TABLE_NAME);
        expenseTagModels = new LinkedHashMap<>();
    }

    private static void populateExpenseTagModelMap() {
        if (expenseTagModels == null) {
            expenseTagModels = new LinkedHashMap<>();
        }
        List<ExpenseTagModel> expenseTags = getAll();
        for (ExpenseTagModel expenseTagModel : expenseTags) {
            expenseTagModels.put(expenseTagModel.getId(), expenseTagModel);
        }
    }

    /**
     * Insert expense tags in table.
     *
     * @param expenseTag
     */
    private static void insert(ExpenseTagModel expenseTag) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ExpenseTag.ID, expenseTag.getId());
        values.put(DatabaseTable.ExpenseTag.NAME, expenseTag.getName());
        values.put(DatabaseTable.ExpenseTag.COLOR, expenseTag.getColor());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ExpenseTag.TABLE_NAME,
                null,
                values
        );
    }

    private static List<ExpenseTagModel> getAll() {
        Log.d(TAG, "Fetching all expense tag");
        List<ExpenseTagModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.ExpenseTag.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseTable.ExpenseTag.NAME
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ExpenseTagModel expenseTagModel = new ExpenseTagModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    );

                    list.add(expenseTagModel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting expense tag " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }
}
