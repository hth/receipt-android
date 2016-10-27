package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.model.helper.BusinessFrequency;
import com.receiptofi.receiptapp.model.helper.Coordinate;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;
import com.receiptofi.receiptapp.utils.Constants;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;
import static com.receiptofi.receiptapp.db.DatabaseTable.ItemReceipt;
import static com.receiptofi.receiptapp.db.DatabaseTable.ShoppingItem;

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
        values.put(ShoppingItem.NAME, shoppingItemModel.getName());
        values.put(ShoppingItem.CUSTOM_NAME, shoppingItemModel.getCustomName());
        values.put(ShoppingItem.BIZ_NAME, shoppingItemModel.getBizName());
        values.put(ShoppingItem.COUNT, shoppingItemModel.getCount());
        values.put(ShoppingItem.SMOOTH_COUNT, shoppingItemModel.getSmoothCount());
        values.put(ShoppingItem.CHECKED, 0);

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                ShoppingItem.TABLE_NAME,
                null,
                values
        );
    }

    protected static void delete(Set<String> bizNames) {
        for (String bizName : bizNames) {
            RDH.getWritableDatabase().delete(
                    ShoppingItem.TABLE_NAME,
                    ShoppingItem.BIZ_NAME + " = '" + bizName + "'",
                    null
            );
        }
    }


    public static List<ShoppingPlace> getBusinessName() {
        List<ShoppingPlace> values = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    true,
                    ShoppingItem.TABLE_NAME,
                    new String[]{ShoppingItem.BIZ_NAME},
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    values.add(new ShoppingPlace(cursor.getString(0)));
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
                        ItemReceipt.TABLE_NAME,
                        new String[]{
                                ItemReceipt.RECEIPT_DATE,
                                ItemReceipt.LAT,
                                ItemReceipt.LNG,
                                ItemReceipt.BIZ_STORE_ADDRESS
                        },
                        ItemReceipt.BIZ_NAME + " = ?",
                        new String[]{bizName},
                        null,
                        null,
                        ItemReceipt.RECEIPT_DATE + " DESC",
                        null);

                if (cursor != null && cursor.getCount() > 0) {
                    BusinessFrequency businessFrequency = new BusinessFrequency(bizName);
                    while (cursor.moveToNext()) {
                        values.add(DateTime.parse(cursor.getString(0), Constants.ISO_J_DF));
                        Coordinate coordinate = new Coordinate(cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
                        businessFrequency.addCoordinates(coordinate);
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
                        ItemReceipt.TABLE_NAME,
                        new String[]{ItemReceipt.NAME, "count()"},
                        ItemReceipt.BIZ_NAME + " = ?",
                        new String[]{bizName},
                        ItemReceipt.NAME,
                        null,
                        ItemReceipt.RECEIPT_DATE + " DESC",
                        null);

                if (cursor != null && cursor.getCount() > 0) {
                    BusinessFrequency businessFrequency = businessFrequencyMap.get(bizName);
                    List<ShoppingItemModel> frequencies = new ArrayList<>();
                    while (cursor.moveToNext()) {
                        ShoppingItemModel shoppingItemModel = new ShoppingItemModel(
                                cursor.getString(0),
                                businessFrequency.getBizName(),
                                cursor.getInt(1),
                                businessFrequency.multiplier(),
                                false);

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

    public static List<ShoppingItemModel> getShoppingItems(String bizName) {
        List<ShoppingItemModel> shoppingItemModels = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    ShoppingItem.TABLE_NAME,
                    null,
                    ShoppingItem.BIZ_NAME + " = ?",
                    new String[]{bizName},
                    null,
                    null,
                    ShoppingItem.CHECKED + " ASC, " +
                            ShoppingItem.SMOOTH_COUNT + " DESC, " +
                            ShoppingItem.CUSTOM_NAME + " ASC, " +
                            ShoppingItem.NAME + " ASC",
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ShoppingItemModel shoppingItemModel = new ShoppingItemModel(
                            cursor.getString(0),
                            cursor.getString(2),
                            cursor.getInt(3),
                            cursor.getDouble(4),
                            cursor.getInt(5) == 1
                    );
                    shoppingItemModel.setCustomName(cursor.getString(1));
                    shoppingItemModels.add(shoppingItemModel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return shoppingItemModels;
    }

    public static boolean updateCheckCondition(String bizName, String name, boolean check) {
        ContentValues uploadValues = new ContentValues();
        uploadValues.put(ShoppingItem.CHECKED, check);
        int update = RDH.getWritableDatabase().update(
                ShoppingItem.TABLE_NAME,
                uploadValues,
                ShoppingItem.BIZ_NAME + "=? and " + ShoppingItem.NAME + "=?",
                new String[]{bizName, name});

        return update > 0;
    }
}
