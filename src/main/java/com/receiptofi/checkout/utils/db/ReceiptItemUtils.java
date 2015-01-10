package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptItemModel;

import java.util.LinkedList;
import java.util.List;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 1/8/15 12:42 AM
 */
public class ReceiptItemUtils {
    private static final String TAG = ReceiptItemUtils.class.getSimpleName();

    public static void insertItems(List<ReceiptItemModel> items) {
        for(ReceiptItemModel item : items) {
            insertItem(item);
        }
    }

    /**
     * Insert item in table.
     *
     * @param item
     */
    private static void insertItem(ReceiptItemModel item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Item.ID, item.getId());
        values.put(DatabaseTable.Item.NAME, item.getName());
        values.put(DatabaseTable.Item.PRICE, item.getPrice());
        values.put(DatabaseTable.Item.QUANTITY, item.getQuantity());
        values.put(DatabaseTable.Item.RECEIPTID, item.getReceiptId());
        values.put(DatabaseTable.Item.SEQUENCE, item.getSequence());
        values.put(DatabaseTable.Item.TAX, item.getTax());
        values.put(DatabaseTable.Item.EXPENSE_TAG, item.getExpenseTag());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Item.TABLE_NAME,
                null,
                values
        );
    }

    public static List<ReceiptItemModel> getItems(String receiptId) {
        Log.d(TAG, "Fetching items for receiptId=" + receiptId);
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
}
