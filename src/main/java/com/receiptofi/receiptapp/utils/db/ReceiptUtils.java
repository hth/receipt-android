package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.collect.Ordering;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ChartModel;
import com.receiptofi.receiptapp.model.FilterGroupObservable;
import com.receiptofi.receiptapp.model.ReceiptGroup;
import com.receiptofi.receiptapp.model.ReceiptGroupHeader;
import com.receiptofi.receiptapp.model.ReceiptModel;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.receiptofi.receiptapp.ReceiptofiApplication.RDH;

public class ReceiptUtils {

    private static final String TAG = ReceiptUtils.class.getSimpleName();
    private static final SimpleDateFormat SDF_YM = new SimpleDateFormat("yyyy-MM-", Locale.US);

    private static final Ordering<String> byYearMonthOrderingDesc = new Ordering<String>() {
        public int compare(String left, String right) {
            return right.compareTo(left);
        }
    };

    private ReceiptUtils() {
    }

    /**
     * Insert receipts in table.
     *
     * @param receipts
     */
    public static void insert(List<ReceiptModel> receipts) {
        for (ReceiptModel receipt : receipts) {
            if (receipt.isDeleted() || !receipt.isActive()) {
                if (delete(receipt.getId())) {
                    if (StringUtils.isBlank(receipt.getReferReceiptId())) {
                        ReceiptItemUtils.delete(receipt.getId());
                    } else {
                        /** Because ITEMs of shared receipts has reference to original receipt id. */
                        ReceiptItemUtils.delete(receipt.getReferReceiptId());
                    }

                    ReceiptSplitUtils.delete(receipt.getId());
                    Log.d(TAG, "Deleted receipt=" + receipt.getId());
                }
            } else {
                insert(receipt);
            }
        }
    }

    private static boolean delete(String receiptId) {
        return RDH.getWritableDatabase().delete(
                DatabaseTable.Receipt.TABLE_NAME,
                DatabaseTable.Receipt.ID + " = '" + receiptId + "'",
                null
        ) > 0;
    }

    /**
     * Insert receipt in table.
     *
     * @param receipt
     */
    private static void insert(ReceiptModel receipt) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Receipt.BIZ_NAME, receipt.getBizName());
        values.put(DatabaseTable.Receipt.BIZ_STORE_ADDRESS, receipt.getAddress());
        values.put(DatabaseTable.Receipt.BIZ_STORE_PHONE, receipt.getPhone());
        values.put(DatabaseTable.Receipt.LAT, receipt.getLat());
        values.put(DatabaseTable.Receipt.LNG, receipt.getLng());
        values.put(DatabaseTable.Receipt.TYPE, receipt.getType());
        values.put(DatabaseTable.Receipt.RATING, receipt.getRating());
        values.put(DatabaseTable.Receipt.RECEIPT_DATE, receipt.getReceiptDate());
        values.put(DatabaseTable.Receipt.EXPENSE_REPORT, receipt.getExpenseReport());
        values.put(DatabaseTable.Receipt.BLOB_IDS, receipt.getBlobIds());
        values.put(DatabaseTable.Receipt.ID, receipt.getId());
        values.put(DatabaseTable.Receipt.NOTES, receipt.getNotes());
        values.put(DatabaseTable.Receipt.PTAX, receipt.getPtax());
        values.put(DatabaseTable.Receipt.RID, receipt.getRid());
        values.put(DatabaseTable.Receipt.TAX, receipt.getTax());
        values.put(DatabaseTable.Receipt.TOTAL, receipt.getTotal());
        values.put(DatabaseTable.Receipt.BILL_STATUS, receipt.getBillStatus());
        values.put(DatabaseTable.Receipt.EXPENSE_TAG_ID, receipt.getExpenseTagId());
        values.put(DatabaseTable.Receipt.REFER_RECEIPT_ID, receipt.getReferReceiptId());
        values.put(DatabaseTable.Receipt.SPLIT_COUNT, receipt.getSplitCount());
        values.put(DatabaseTable.Receipt.SPLIT_TOTAL, receipt.getSplitTotal());
        values.put(DatabaseTable.Receipt.SPLIT_TAX, receipt.getSplitTax());
        values.put(DatabaseTable.Receipt.ACTIVE, receipt.isActive());
        values.put(DatabaseTable.Receipt.DELETED, receipt.isDeleted());

        ReceiptofiApplication.RDH.getWritableDatabase().delete(
                DatabaseTable.Receipt.TABLE_NAME,
                "id = ?",
                new String[]{receipt.getId()}
        );

        ReceiptofiApplication.RDH.getWritableDatabase().delete(
                DatabaseTable.Item.TABLE_NAME,
                DatabaseTable.Item.RECEIPTID + " = ?",
                new String[]{receipt.getId()}
        );

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                values
        );
    }

    public static ChartModel getReceiptsByBizName() {
        ChartModel chartModel = new ChartModel();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "select "
                            + "bizName,"
                            + "total(total) total "
                            + "from " + DatabaseTable.Receipt.TABLE_NAME + " "
                            + "where " + DatabaseTable.Receipt.RECEIPT_DATE + " between "
                            + "datetime('now', 'localtime', 'start of month') AND "
                            + "datetime('now', 'localtime', 'start of month','+1 month','-1 day') "
                            + "group by " + DatabaseTable.Receipt.BIZ_NAME, null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ReceiptModel receiptModel = new ReceiptModel();
                    receiptModel.setBizName(cursor.getString(0));
                    receiptModel.setTotal(cursor.getDouble(1));

                    chartModel.addReceiptModel(receiptModel);
                    chartModel.addTotal(receiptModel.getTotal());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error get chart model " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return chartModel;
    }

    public static List<ReceiptModel> fetchReceipts(String year, String month) {
        Log.d(TAG, "Fetching receipt for year=" + year + " month=" + month);

        List<ReceiptModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    null,
                    "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 6, 2) = ? and " +
                            "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 1, 4) = ? ",
                    new String[]{month, year},
                    null,
                    null,
                    DatabaseTable.Receipt.RECEIPT_DATE + " DESC"
            );

            list = retrieveReceiptModelFromCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, "Error getting receipts " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }

    public static Double findSplitTotal(String bizName, String receiptDate) {
        Log.d(TAG, "Fetching receipt for bizName=" + bizName + " receiptDate=" + receiptDate);

        Double splitTotal = 0.0;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    new String[]{DatabaseTable.Receipt.SPLIT_TOTAL},
                    DatabaseTable.Receipt.BIZ_NAME + "=? and " + DatabaseTable.Receipt.RECEIPT_DATE + "=?",
                    new String[]{bizName, receiptDate},
                    null,
                    null,
                    DatabaseTable.Receipt.RECEIPT_DATE + " DESC"
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    splitTotal = cursor.getDouble(0);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting receipts " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return splitTotal;
    }

    /**
     * Fetch receipts based on receipt ids.
     *
     * @param ids
     * @return
     */
    public static List<ReceiptModel> fetchReceipts(List<String> ids) {
        Log.d(TAG, "Get receipts for ids");

        StringBuilder selection = new StringBuilder();
        String or = "";
        for (String id : ids) {
            selection.append(or).append(DatabaseTable.Receipt.ID + " = ?");
            or = " OR ";
        }

        List<ReceiptModel> list = new LinkedList<>();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    null,
                    selection.toString(),
                    ids.toArray(new String[ids.size()]),
                    null,
                    null,
                    DatabaseTable.Receipt.RECEIPT_DATE + " DESC"
            );

            list = retrieveReceiptModelFromCursor(cursor);
        } catch (Exception e) {
            Log.e(TAG, "Error getting receipts " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }

    /**
     * Fetch receipts based on receipt id.
     *
     * @param id
     * @return
     */
    public static ReceiptModel fetchReceipt(String id) {
        Log.d(TAG, "Get receipts for ids");
        ReceiptModel receiptModel = null;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    null,
                    DatabaseTable.Receipt.ID + "=?",
                    new String[]{id},
                    null,
                    null,
                    null
            );

            List<ReceiptModel> list = retrieveReceiptModelFromCursor(cursor);
            if (!list.isEmpty()) {
                receiptModel = list.get(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return receiptModel;
    }

    /**
     * Fetch receipts for selected business name in pie chart. This is used as filter receipts by business name
     * for selected month in pie chart.
     *
     * @param bizName
     * @param monthYear
     * @return
     */
    public static ReceiptGroup filterByBizByMonth(String bizName, Date monthYear) {
        String yearMonth = SDF_YM.format(monthYear);
        ReceiptGroup receiptGroup = ReceiptGroup.getInstance();
        Cursor cursor = null;

        try {
            String escapedBizName = DBUtils.sqlEscapeString(bizName);
            /**
             * select * from RECEIPT where bizName = 'Costco' and receiptDate LIKE '2015-01-%'
             */
            cursor = RDH.getReadableDatabase().rawQuery(
                    "select * from "
                            + DatabaseTable.Receipt.TABLE_NAME + " where "
                            + DatabaseTable.Receipt.BIZ_NAME + " = " + escapedBizName + " "
                            + "and " + DatabaseTable.Receipt.RECEIPT_DATE + " LIKE '" + yearMonth + "%' "
                            + "ORDER BY " + DatabaseTable.Receipt.RECEIPT_DATE + " DESC",
                    null);
            receiptGroup.addReceiptGroup(retrieveReceiptModelFromCursor(cursor));

            cursor = RDH.getReadableDatabase().rawQuery(
                    "select "
                            + "total(" + DatabaseTable.Receipt.SPLIT_TOTAL + ") " + DatabaseTable.Receipt.SPLIT_TOTAL + " "
                            + "from " + DatabaseTable.Receipt.TABLE_NAME + " "
                            + "where " + DatabaseTable.Receipt.BIZ_NAME + " = " + escapedBizName + " "
                            + "and " + DatabaseTable.Receipt.RECEIPT_DATE + " LIKE '" + SDF_YM.format(monthYear) + "%'",
                    null);

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String[] yearMonthSplit = yearMonth.split(Pattern.quote("-"));
                    ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                            yearMonthSplit[1],
                            yearMonthSplit[0],
                            cursor.getDouble(0),
                            receiptGroup.getReceiptModels().get(0).size());
                    receiptGroup.addReceiptGroupHeader(receiptGroupHeader);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during filter by Biz by Month " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return receiptGroup;
    }

    /**
     * Search receipts and items by keyword.
     *
     * @param name
     * @return
     */
    public static ReceiptGroup searchByKeyword(String name) {
        Log.d(TAG, "Search for " + name);
        ReceiptGroup receiptGroup = ReceiptGroup.getInstance();
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Item.TABLE_NAME,
                    new String[]{DatabaseTable.Item.RECEIPTID},
                    DatabaseTable.Item.NAME + " LIKE ?",
                    new String[]{"%" + name + "%"},
                    null,
                    null,
                    null
            );

            List<String> receiptIds = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    receiptIds.add(cursor.getString(0));
                }
            }

            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    new String[]{DatabaseTable.Receipt.ID},
                    DatabaseTable.Receipt.BIZ_NAME + " LIKE ?",
                    new String[]{"%" + name + "%"},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    receiptIds.add(cursor.getString(0));
                }
            }

            String ids = "";
            for (String id : receiptIds) {
                ids += "'" + id + "',";
            }

            if (ids.length() > 0) {
                cursor = RDH.getReadableDatabase().rawQuery(
                        "select "
                                + "* "
                                + "from " + DatabaseTable.Receipt.TABLE_NAME + " "
                                + "where " + DatabaseTable.Receipt.ID
                                + " IN (" + ids.substring(0, ids.length() - 1) + ")"
                                + " OR " + DatabaseTable.Receipt.REFER_RECEIPT_ID
                                + " IN (" + ids.substring(0, ids.length() - 1) + ")"
                                + "ORDER BY " + DatabaseTable.Receipt.RECEIPT_DATE + " DESC",
                        null);

                receiptGroup = convertToReceiptGroup(retrieveReceiptModelFromCursor(cursor));

            }
        } catch (Exception e) {
            Log.e(TAG, "Error searching by keyword " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        FilterGroupObservable.setMonthlyReceiptGroup(receiptGroup);
        FilterGroupObservable.setKeyWord(name);
        return receiptGroup;
    }

    private static ReceiptGroup convertToReceiptGroup(List<ReceiptModel> receiptModels) {
        ReceiptGroup receiptGroup = ReceiptGroup.getInstance();

        Map<String, List<ReceiptModel>> map = new HashMap<>();
        for (ReceiptModel receiptModel : receiptModels) {
            String yearMonth = receiptModel.getReceiptYearMonth();
            if (map.get(yearMonth) == null) {
                List<ReceiptModel> receipts = new LinkedList<>();
                receipts.add(receiptModel);
                map.put(yearMonth, receipts);
            } else {
                List<ReceiptModel> receipts = map.get(yearMonth);
                receipts.add(receiptModel);
            }
        }

        List<String> sortedKeys = byYearMonthOrderingDesc.sortedCopy(map.keySet());
        for (String key : sortedKeys) {
            receiptGroup.addReceiptGroup(map.get(key));
            ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                    key.split("\\-")[1],
                    key.split("\\-")[0],
                    null,
                    map.get(key).size());
            receiptGroup.addReceiptGroupHeader(receiptGroupHeader);
        }

        return receiptGroup;
    }

    private static List<ReceiptModel> retrieveReceiptModelFromCursor(Cursor cursor) {
        List<ReceiptModel> list = new LinkedList<>();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ReceiptModel receiptModel = new ReceiptModel();
                receiptModel.setBizName(cursor.getString(0));
                receiptModel.setAddress(cursor.getString(1));
                receiptModel.setPhone(cursor.getString(2));
                receiptModel.setLat(cursor.getDouble(3));
                receiptModel.setLng(cursor.getDouble(4));
                receiptModel.setType(cursor.getString(5));
                receiptModel.setRating(cursor.getDouble(6));
                receiptModel.setReceiptDate(cursor.getString(7));
                receiptModel.setExpenseReport(cursor.getString(8));
                receiptModel.setBlobIds(cursor.getString(9));
                receiptModel.setId(cursor.getString(10));
                receiptModel.setNotes(cursor.getString(11));
                receiptModel.setPtax(cursor.getDouble(12));
                receiptModel.setRid(cursor.getString(13));
                receiptModel.setTax(cursor.getDouble(14));
                receiptModel.setTotal(cursor.getDouble(15));
                receiptModel.setBillStatus(cursor.getString(16));
                receiptModel.setExpenseTagId(cursor.getString(17));
                receiptModel.setReferReceiptId(cursor.getString(18));
                receiptModel.setSplitCount(cursor.getInt(19));
                receiptModel.setSplitTotal(cursor.getDouble(20));
                receiptModel.setSplitTax(cursor.getDouble(21));
                receiptModel.setActive(cursor.getInt(22) == 1);
                receiptModel.setDeleted(cursor.getInt(23) == 1);

                if (!TextUtils.isEmpty(receiptModel.getExpenseTagId())) {
                    receiptModel.setExpenseTagModel(ExpenseTagUtils.getExpenseTagModels().get(receiptModel.getExpenseTagId()));
                }

                if (StringUtils.isBlank(receiptModel.getReferReceiptId())) {
                    receiptModel.setReceiptItems(ReceiptItemUtils.getItems(receiptModel.getId()));
                } else {
                    receiptModel.setReceiptItems(ReceiptItemUtils.getItems(receiptModel.getReferReceiptId()));
                }
                list.add(receiptModel);
            }
        }

        return list;
    }
}
