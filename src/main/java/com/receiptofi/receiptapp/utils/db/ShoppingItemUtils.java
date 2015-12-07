package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.model.helper.BusinessFrequency;
import com.receiptofi.receiptapp.model.helper.Coordinates;
import com.receiptofi.receiptapp.utils.Constants;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 12/6/15 4:56 PM
 */
public class ShoppingItemUtils {
    private static final String TAG = ItemReceiptUtils.class.getSimpleName();

    private static Ordering<ShoppingItemModel> SORT_BY_SMOOTH_COUNT = new Ordering<ShoppingItemModel>() {
        public int compare(ShoppingItemModel right, ShoppingItemModel left) {
            return Double.compare(right.getSmoothCount(), left.getSmoothCount());
        }
    };

    private static Ordering<ShoppingItemModel> SORT_BY_NAME = new Ordering<ShoppingItemModel>() {
        public int compare(ShoppingItemModel right, ShoppingItemModel left) {
            return right.getName().compareTo(left.getName());
        }
    };

    private static Ordering<ShoppingItemModel> SORT_SHOPPING_LIST = SORT_BY_SMOOTH_COUNT.reverse().compound(SORT_BY_NAME);

    public static void insert(Set<String> bizNames) {
        Map<String, BusinessFrequency> businessFrequencyMap = businessFrequency(bizNames);
        Map<BusinessFrequency, List<ShoppingItemModel>> shoppingList = generateList(businessFrequencyMap);

        for (BusinessFrequency businessFrequency : shoppingList.keySet()) {
            for (ShoppingItemModel shoppingItemModel : shoppingList.get(businessFrequency)) {
                insert(shoppingItemModel);
            }
        }

        printList(shoppingList);
    }

    public static void printList(Map<BusinessFrequency, List<ShoppingItemModel>> shoppingList) {
        for (BusinessFrequency frequency : shoppingList.keySet()) {
            List<ShoppingItemModel> frequencies = shoppingList.get(frequency);
            for (ShoppingItemModel shoppingItemModel : frequencies) {
                Log.i(TAG, "" + shoppingItemModel);
            }
        }
    }

    /**
     * Insert itemReceiptModel in table.
     *
     * @param shoppingItemModel
     */
    private static void insert(ShoppingItemModel shoppingItemModel) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.ShoppingItem.NAME, shoppingItemModel.getName());
        values.put(DatabaseTable.ShoppingItem.CUSTOM_NAME, shoppingItemModel.getCustomName());
        values.put(DatabaseTable.ShoppingItem.BIZ_NAME, shoppingItemModel.getBizName());
        values.put(DatabaseTable.ShoppingItem.COUNT, shoppingItemModel.getCount());
        values.put(DatabaseTable.ShoppingItem.SMOOTH_COUNT, shoppingItemModel.getSmoothCount());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.ShoppingItem.TABLE_NAME,
                null,
                values
        );
    }

    protected static void delete(Set<String> bizNames) {
        for (String bizName : bizNames) {
            RDH.getWritableDatabase().delete(
                    DatabaseTable.ShoppingItem.TABLE_NAME,
                    DatabaseTable.ShoppingItem.BIZ_NAME + " = '" + bizName + "'",
                    null
            );
        }
    }


    private static List<String> getBusinessName() {
        List<String> values = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    true,
                    DatabaseTable.ItemReceipt.TABLE_NAME,
                    new String[]{DatabaseTable.ItemReceipt.BIZ_NAME},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    values.add(cursor.getString(0));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return values;
    }

    /**
     * Find business frequency.
     *
     * @param bizNames
     * @return
     */
    private static Map<String, BusinessFrequency> businessFrequency(Set<String> bizNames) {
        Map<String, BusinessFrequency> businessFrequencies = new HashMap<>();
        Cursor cursor = null;
        for (String bizName : bizNames) {
            try {
                List<DateTime> values = new ArrayList<>();
                cursor = RDH.getReadableDatabase().query(
                        true,
                        DatabaseTable.ItemReceipt.TABLE_NAME,
                        new String[]{
                                DatabaseTable.ItemReceipt.RECEIPT_DATE,
                                DatabaseTable.ItemReceipt.LAT,
                                DatabaseTable.ItemReceipt.LNG
                        },
                        DatabaseTable.ItemReceipt.BIZ_NAME + " = ?",
                        new String[]{bizName},
                        null,
                        null,
                        DatabaseTable.ItemReceipt.RECEIPT_DATE + " DESC",
                        null);

                if (cursor != null && cursor.getCount() > 0) {
                    BusinessFrequency businessFrequency = new BusinessFrequency(bizName);
                    while (cursor.moveToNext()) {
                        values.add(DateTime.parse(cursor.getString(0), Constants.ISO_J_DF));
                        Coordinates coordinates = new Coordinates(cursor.getDouble(1), cursor.getDouble(2));
                        businessFrequency.addCoordinates(coordinates);
                    }

                    int visitFrequency = 0;
                    DateTime since = DateTime.now().minusWeeks(BusinessFrequency.WEEKS);
                    for (DateTime date : values) {
                        if (date.isAfter(since)) {
                            /** How often this business was visited in last BusinessFrequency.WEEKS. */
                            visitFrequency++;
                            businessFrequency.addVisit(date);
                        }
                    }

                    if (visitFrequency > 0) {
                        businessFrequency.setFrequency(visitFrequency);
                        businessFrequency.setWeeksOfShoppingHistory(Weeks.weeksBetween(values.get(values.size() - 1), DateTime.now()).getWeeks());

                        businessFrequencies.put(bizName, businessFrequency);
                    } else {
                        Log.i(TAG, "Has visited " + bizName + " before " + since);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return businessFrequencies;
    }

    private static Map<BusinessFrequency, List<ShoppingItemModel>> generateList(Map<String, BusinessFrequency> businessFrequencyMap) {
        Map<BusinessFrequency, List<ShoppingItemModel>> businessItemFrequency = new HashMap<>();
        Cursor cursor = null;
        for (String bizName : businessFrequencyMap.keySet()) {
            try {
                cursor = RDH.getReadableDatabase().query(
                        DatabaseTable.ItemReceipt.TABLE_NAME,
                        new String[]{DatabaseTable.ItemReceipt.NAME, "count()"},
                        DatabaseTable.ItemReceipt.BIZ_NAME + " = ?",
                        new String[]{bizName},
                        DatabaseTable.ItemReceipt.NAME,
                        null,
                        DatabaseTable.ItemReceipt.RECEIPT_DATE + " DESC",
                        null);

                if (cursor != null && cursor.getCount() > 0) {
                    BusinessFrequency businessFrequency = businessFrequencyMap.get(bizName);
                    List<ShoppingItemModel> frequencies = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        ShoppingItemModel shoppingItemModel = new ShoppingItemModel(
                                cursor.getString(0),
                                businessFrequency.getBizName(),
                                cursor.getInt(1),
                                businessFrequency.multiplier());

                        frequencies.add(shoppingItemModel);
                    }

                    businessItemFrequency.put(businessFrequency, SORT_SHOPPING_LIST.sortedCopy(frequencies));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }
        }

        return businessItemFrequency;
    }
}
