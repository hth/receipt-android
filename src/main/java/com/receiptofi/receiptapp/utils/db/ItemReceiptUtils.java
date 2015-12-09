package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ItemReceiptModel;
import com.receiptofi.receiptapp.model.helper.Coordinate;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;
import com.receiptofi.receiptapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 12/3/15 12:37 AM
 */
public class ItemReceiptUtils {
    private static final String TAG = ItemReceiptUtils.class.getSimpleName();

    private static Ordering<ShoppingPlace> SORT_BY_DATE = new Ordering<ShoppingPlace>() {
        public int compare(ShoppingPlace right, ShoppingPlace left) {
            return right.getLastShopped().get(0).compareTo(left.getLastShopped().get(0));
        }
    };

    private ItemReceiptUtils() {
    }

    public static void insert(List<ItemReceiptModel> itemReceiptModels) {
        Set<String> insertBizNames = new HashSet<>();
        Set<String> deleteBizNames = new HashSet<>();

        for (ItemReceiptModel itemReceiptModel : itemReceiptModels) {
            /** Only add items that were bought and not returned. */
            if (itemReceiptModel.isActive() && itemReceiptModel.getPrice() >= 0.0) {
                insert(itemReceiptModel);
                insertBizNames.add(itemReceiptModel.getBizName());
            } else {
                delete(itemReceiptModel.getReceiptId());
                deleteBizNames.add(itemReceiptModel.getBizName());
            }
        }

        ShoppingItemUtils.delete(deleteBizNames);
        ShoppingItemUtils.insert(insertBizNames);
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

    public static List<ShoppingPlace> populateShoppingPlaces(List<ShoppingPlace> shoppingPlaces) {
        List<ShoppingPlace> shoppingPlaceList = new ArrayList<>();
        Cursor cursor = null;
        for (ShoppingPlace shoppingPlace : shoppingPlaces) {
            try {
                cursor = RDH.getReadableDatabase().query(
                        true,
                        DatabaseTable.ItemReceipt.TABLE_NAME,
                        new String[]{
                                DatabaseTable.ItemReceipt.RECEIPT_DATE,
                                DatabaseTable.ItemReceipt.LAT,
                                DatabaseTable.ItemReceipt.LNG
                        },
                        DatabaseTable.ItemReceipt.BIZ_NAME + " = ?",
                        new String[]{shoppingPlace.getBizName()},
                        null,
                        null,
                        DatabaseTable.ItemReceipt.RECEIPT_DATE + " DESC",
                        null);

                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        shoppingPlace.addLastShopped(AppUtils.getDateTime(cursor.getString(0)).toDate());
                        Coordinate coordinate = new Coordinate(cursor.getDouble(1), cursor.getDouble(2));
                        shoppingPlace.addCoordinates(coordinate);
                    }

                    shoppingPlaceList.add(shoppingPlace);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }
        return SORT_BY_DATE.reverse().sortedCopy(shoppingPlaceList);
    }
}
