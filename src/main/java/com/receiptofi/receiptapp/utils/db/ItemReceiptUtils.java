package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;

import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ItemReceiptModel;

import java.util.List;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 12/3/15 12:37 AM
 */
public class ItemReceiptUtils {
    private static final String TAG = ItemReceiptUtils.class.getSimpleName();

    private ItemReceiptUtils() {
    }

    public static void insert(List<ItemReceiptModel> itemReceiptModels) {
        for (ItemReceiptModel itemReceiptModel : itemReceiptModels) {
            if (itemReceiptModel.isActive()) {
                insert(itemReceiptModel);
            } else {
                delete(itemReceiptModel.getReceiptId());
            }
        }
    }

    protected static void delete(String receiptId) {
        RDH.getWritableDatabase().delete(
                DatabaseTable.ItemReceipt.TABLE_NAME,
                DatabaseTable.ItemReceipt.RECEIPT_ID + " = '" + receiptId + "'",
                null
        );
    }

    /**
     * Insert itemReceiptModel in table.
     *
     * @param itemReceiptModel
     */
    private static void insert(ItemReceiptModel itemReceiptModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ItemReceipt.RECEIPT_ID, itemReceiptModel.getReceiptId());
        values.put(DatabaseTable.ItemReceipt.BIZ_NAME, itemReceiptModel.getBizName());
        values.put(DatabaseTable.ItemReceipt.LAT, itemReceiptModel.getLat());
        values.put(DatabaseTable.ItemReceipt.LNG, itemReceiptModel.getLng());
        values.put(DatabaseTable.ItemReceipt.RECEIPT_DATE, itemReceiptModel.getReceiptDate());
        values.put(DatabaseTable.ItemReceipt.EXPENSE_TAG_ID, itemReceiptModel.getExpenseTagId());
        values.put(DatabaseTable.ItemReceipt.ITEM_ID, itemReceiptModel.getItemId());
        values.put(DatabaseTable.ItemReceipt.NAME, itemReceiptModel.getName());
        values.put(DatabaseTable.ItemReceipt.PRICE, itemReceiptModel.getPrice());
        values.put(DatabaseTable.ItemReceipt.QUANTITY, itemReceiptModel.getQuantity());
        values.put(DatabaseTable.ItemReceipt.TAX, itemReceiptModel.getTax());
        values.put(DatabaseTable.ItemReceipt.ACTIVE, itemReceiptModel.isActive());
        values.put(DatabaseTable.ItemReceipt.DELETED, itemReceiptModel.isDeleted());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ItemReceipt.TABLE_NAME,
                null,
                values
        );
    }
}
