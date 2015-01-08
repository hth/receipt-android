package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptItemModel;

import java.util.List;

/**
 * User: hitender
 * Date: 1/8/15 12:42 AM
 */
public class ReceiptItemUtils {

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

        ReceiptofiApplication.RDH.getWritableDatabase().delete(
                DatabaseTable.Item.TABLE_NAME,
                DatabaseTable.Item.RECEIPTID + " = ?",
                new String[]{item.getReceiptId()}
        );
        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Item.TABLE_NAME,
                null,
                values
        );
    }
}
