package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ExpenseTagModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 1/24/15 1:03 AM
 */
public class ExpenseTagUtils {

    private static final String TAG = ExpenseTagUtils.class.getSimpleName();
    private static Map<String, ExpenseTagModel> expenseTagModels = new LinkedHashMap<>();

    private ExpenseTagUtils() {
    }

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

    public static boolean insert(List<ExpenseTagModel> expensesTags) {
        Boolean modified = false;
        List<String> ids = getAllIds();
        for (ExpenseTagModel expenseTag : expensesTags) {
            if (expenseTag.isDeleted()) {
                delete(expenseTag.getId());
                modified = true;
            } else {
                if (ids.contains(expenseTag.getId())) {
                    update(expenseTag);
                    modified = true;
                } else {
                    insert(expenseTag);
                    modified = true;
                }

            }
        }

        expenseTagModels = null;
        populateExpenseTagModelMap();
        return modified;
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

    public static void updateExpenseTag(String id, String tag, String color, String icon) {
        Log.d(TAG, "Updating expense tag before updating on server");
        update(new ExpenseTagModel(id, tag, color, icon, false));
        populateExpenseTagModelMap();
    }

    /**
     * Insert expense tags in table.
     *
     * @param expenseTag
     */
    private static void insert(ExpenseTagModel expenseTag) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ExpenseTag.ID, expenseTag.getId());
        values.put(DatabaseTable.ExpenseTag.TAG, expenseTag.getTag());
        values.put(DatabaseTable.ExpenseTag.COLOR, expenseTag.getColor());
        values.put(DatabaseTable.ExpenseTag.ICON, expenseTag.getIcon());
        values.put(DatabaseTable.ExpenseTag.DELETED, expenseTag.isDeleted());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ExpenseTag.TABLE_NAME,
                null,
                values
        );
    }

    private static void delete(String expenseTagId) {
        RDH.getWritableDatabase().delete(
                DatabaseTable.ExpenseTag.TABLE_NAME,
                DatabaseTable.ExpenseTag.ID + " = '" + expenseTagId + "'",
                null
        );
    }

    private static List<String> getAllIds() {
        Log.d(TAG, "Fetching all expense tag ids");
        List<String> ids = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.ExpenseTag.TABLE_NAME,
                    new String[]{DatabaseTable.ExpenseTag.ID},
                    null,
                    null,
                    null,
                    null,
                    DatabaseTable.ExpenseTag.TAG
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ids.add(cursor.getString(0));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting expense tag ids " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return ids;
    }

    private static void update(ExpenseTagModel expenseTag) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ExpenseTag.TAG, expenseTag.getTag());
        values.put(DatabaseTable.ExpenseTag.COLOR, expenseTag.getColor());
        values.put(DatabaseTable.ExpenseTag.ICON, expenseTag.getIcon());
        values.put(DatabaseTable.ExpenseTag.DELETED, expenseTag.isDeleted());
        String[] whereArgs = {expenseTag.getId()};

        ReceiptofiApplication.RDH.getWritableDatabase().update(
                DatabaseTable.ExpenseTag.TABLE_NAME,
                values,
                DatabaseTable.ExpenseTag.ID + "=?",
                whereArgs
        );
    }

    public static List<ExpenseTagModel> getAll() {
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
                    DatabaseTable.ExpenseTag.TAG
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ExpenseTagModel expenseTagModel = new ExpenseTagModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4) == 1
                    );

                    Log.d(TAG, expenseTagModel.toString());
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
