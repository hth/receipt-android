package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ReceiptItemModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 1/8/15 12:42 AM
 */
public class ReceiptItemUtils {
    private static final String TAG = ReceiptItemUtils.class.getSimpleName();

    private ReceiptItemUtils() {
    }

    public static void insert(List<ReceiptItemModel> items) {
        for (ReceiptItemModel item : items) {
            insert(item);
        }
    }

    protected static void delete(String receiptId) {
        RDH.getWritableDatabase().delete(
                DatabaseTable.Item.TABLE_NAME,
                DatabaseTable.Item.RECEIPTID + " = '" + receiptId + "'",
                null
        );
    }

    /**
     * Insert item in table.
     *
     * @param item
     */
    private static void insert(ReceiptItemModel item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Item.ID, item.getId());
        values.put(DatabaseTable.Item.NAME, item.getName());
        values.put(DatabaseTable.Item.PRICE, item.getPrice());
        values.put(DatabaseTable.Item.QUANTITY, item.getQuantity());
        values.put(DatabaseTable.Item.RECEIPTID, item.getReceiptId());
        values.put(DatabaseTable.Item.SEQUENCE, item.getSequence());
        values.put(DatabaseTable.Item.TAX, item.getTax());
        values.put(DatabaseTable.Item.EXPENSE_TAG_ID, item.getExpenseTagId());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Item.TABLE_NAME,
                null,
                values
        );
    }

    public static List<ReceiptItemModel> getItems(String receiptId) {
        Log.d(TAG, "Fetching items for receiptId=" + receiptId);

        List<ReceiptItemModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Item.TABLE_NAME,
                    null,
                    DatabaseTable.Item.RECEIPTID + " = ?",
                    new String[]{receiptId},
                    null,
                    null,
                    DatabaseTable.Item.SEQUENCE + " ASC"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ReceiptItemModel receiptItemModel = new ReceiptItemModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getString(7)
                    );

                    list.add(receiptItemModel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting items for receipt " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }

    /**
     * Finds receipt id for matching item name search.
     *
     * @param name
     * @return
     */
    public static List<String> searchReceiptWithItemName(String name) {
        Log.d(TAG, "Fetching items matching name=" + name);

        List<String> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    true,
                    DatabaseTable.Item.TABLE_NAME,
                    new String[]{DatabaseTable.Item.RECEIPTID},
                    DatabaseTable.Item.NAME + " LIKE '?'",
                    new String[]{"%" + name + "%"},
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(0));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching items with name " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }
}
