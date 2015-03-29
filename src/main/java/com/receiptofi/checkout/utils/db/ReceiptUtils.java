package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.http.types.Protocol;
import com.receiptofi.checkout.model.ChartModel;
import com.receiptofi.checkout.model.ReceiptGroup;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.UnprocessedDocumentModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.KEYS;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.updateInsert;

public class ReceiptUtils {

    private static final String TAG = ReceiptUtils.class.getSimpleName();
    private static final SimpleDateFormat SDF_YM = new SimpleDateFormat("yyyy-MM-");

    public static void getUnprocessedCount() {

        ExternalCall.doGet(API.UNPROCESSED_COUNT_API, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                Message msg = new Message();
                msg.what = HomeActivity.UPDATE_UNPROCESSED_COUNT;
                UnprocessedDocumentModel unprocessedDocumentModel = JsonParseUtils.parseUnprocessedDocument(body);
                msg.obj = unprocessedDocumentModel.getCount();
                updateInsert(KEYS.UNPROCESSED_DOCUMENT, unprocessedDocumentModel.getCount());
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(int statusCode, String error) {

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }

    public static void getAllReceipts() {
        ExternalCall.doGet(API.GET_ALL_RECEIPTS, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                Message msg = new Message();
                msg.what = HomeActivity.GET_ALL_RECEIPTS;
                insert(JsonParseUtils.parseReceipts(body));
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                }

                //Remove this method call
                //MonthlyReportUtils.computeMonthlyReceiptReport();
            }

            @Override
            public void onError(int statusCode, String error) {

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }


    public static void fetchReceiptsAndSave() {

        ArrayList<NameValuePair> headerData = new ArrayList<>();
        headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
        headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));

        ExternalCall.AsyncRequest(
                headerData,
                API.GET_ALL_RECEIPTS,
                Protocol.GET.name(),
                new ResponseHandler() {
                    @Override
                    public void onSuccess(Header[] arr, String body) {
                        insert(ResponseParser.getReceipts(body));
                    }

                    @Override
                    public void onException(Exception exception) {

                    }

                    @Override
                    public void onError(int statusCode, String error) {

                    }
                });
    }

    public static void clearReceipts() {
        ReceiptofiApplication.RDH.getWritableDatabase().rawQuery("delete from " + DatabaseTable.Receipt.TABLE_NAME + ";", null);
    }

    public static ArrayList<ReceiptModel> getAllReceipts_old() {

        String[] columns = new String[]{DatabaseTable.Receipt.BIZ_NAME, DatabaseTable.Receipt.RECEIPT_DATE, DatabaseTable.Receipt.PTAX, DatabaseTable.Receipt.TOTAL, DatabaseTable.Receipt.ID, DatabaseTable.Receipt.BLOB_IDS};
        Cursor receiptsRecords =
                ReceiptofiApplication.RDH.getReadableDatabase().query(
                    DatabaseTable.Receipt.TABLE_NAME,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    null
                );

        ArrayList<ReceiptModel> rModels = new ArrayList<>();
        if (receiptsRecords != null && receiptsRecords.getCount() > 0) {
            for (receiptsRecords.moveToFirst(); !receiptsRecords.isAfterLast(); receiptsRecords.moveToNext()) {
                ReceiptModel model = new ReceiptModel();
                model.setBizName(receiptsRecords.getString(0));
                model.setReceiptDate(receiptsRecords.getString(1));
                model.setPtax(receiptsRecords.getDouble(2));
                model.setTotal(receiptsRecords.getDouble(3));
                model.setId(receiptsRecords.getString(4));
                model.setBlobIds(receiptsRecords.getString(5));
                rModels.add(model);
            }

        }
        if (null != receiptsRecords) {
            receiptsRecords.close();
        }
        return rModels;

    }

    /**
     * Insert receipts in table.
     *
     * @param receipts
     */
    public static void insert(List<ReceiptModel> receipts) {
        for (ReceiptModel receipt : receipts) {
            insert(receipt);
        }
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
        Cursor cursor = RDH.getReadableDatabase().rawQuery(
                "select " +
                        "bizName," +
                        "total(total) total " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " " +
                        "where " + DatabaseTable.Receipt.RECEIPT_DATE + " between " +
                        "datetime('now', 'start of month') AND " +
                        "datetime('now', 'start of month','+1 month','-1 day') " +
                        "group by bizName", null);

        ChartModel chartModel = new ChartModel();
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ReceiptModel receiptModel = new ReceiptModel();
                receiptModel.setBizName(cursor.getString(0));
                receiptModel.setTotal(cursor.getDouble(1));

                chartModel.addReceiptModel(receiptModel);
                chartModel.addTotal(receiptModel.getTotal());
            }
        }
        return chartModel;
    }

    public static List<ReceiptModel> fetchReceipts(String year, String month) {
        Log.d(TAG, "Fetching receipt for year=" + year + " month=" + month);

        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 6, 2) = ? and SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 1, 4) = ? ",
                new String[]{month, year},
                null,
                null,
                DatabaseTable.Receipt.RECEIPT_DATE + " desc"
        );

        return retrieveReceiptModelFromCursor(cursor);
    }

    /**
     * Used for map drill down when bizName is provided.
     *
     * @param year
     * @param month
     * @param bizName not null
     * @return
     */
    public static List<ReceiptModel> fetchReceiptsForBizName(String year, String month, String bizName) {
        Log.d(TAG, "Fetching receipt for year=" + year + " month=" + month + " bizName=" + bizName);

        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                "SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 6, 2) = ? and SUBSTR(" + DatabaseTable.Receipt.RECEIPT_DATE + ", 1, 4) = ? and bizName = ? ",
                new String[]{month, year, bizName},
                null,
                null,
                DatabaseTable.Receipt.RECEIPT_DATE + " desc"
        );

        return retrieveReceiptModelFromCursor(cursor);
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

        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                selection.toString(),
                ids.toArray(new String[ids.size()]),
                null,
                null,
                DatabaseTable.Receipt.RECEIPT_DATE + " desc"
        );

        return retrieveReceiptModelFromCursor(cursor);
    }

    /**
     * select * from RECEIPT where bizName = 'Costco' and receiptDate LIKE '2015-01-%'
     *
     * @param bizName
     * @param monthYear
     * @return
     */
    public static ReceiptGroup filterByBizByMonth(String bizName, Date monthYear) {
        String yearMonth = SDF_YM.format(monthYear);
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                DatabaseTable.Receipt.BIZ_NAME + " = ? and " +
                        DatabaseTable.Receipt.RECEIPT_DATE + " LIKE ?",
                new String[]{bizName, yearMonth + "%"},
                null,
                null,
                DatabaseTable.Receipt.RECEIPT_DATE + " desc"
        );

        ReceiptGroup receiptGroup = ReceiptGroup.getInstance();
        receiptGroup.addReceiptGroup(retrieveReceiptModelFromCursor(cursor));

        cursor = RDH.getReadableDatabase().rawQuery(
                "select " +
                        "total(total) total " +
                        "from " + DatabaseTable.Receipt.TABLE_NAME + " " +
                        "where " + DatabaseTable.Receipt.BIZ_NAME + " = '" + bizName + "' " +
                        "and " + DatabaseTable.Receipt.RECEIPT_DATE + " LIKE  '" + SDF_YM.format(monthYear) + "%'", null);

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String[] yearMonthSplit = yearMonth.split("-");
                ReceiptGroupHeader receiptGroupHeader = new ReceiptGroupHeader(
                        yearMonthSplit[1],
                        yearMonthSplit[0],
                        cursor.getDouble(0),
                        receiptGroup.getReceiptModels().get(0).size());
                receiptGroup.addReceiptGroupHeader(receiptGroupHeader);
            }
        }

        return receiptGroup;
    }

    /**
     * Search receipts and items with name.
     *
     * @param name
     * @return
     */
    public static ReceiptGroup searchByName(String name) {
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Item.TABLE_NAME,
                new String[] {DatabaseTable.Item.RECEIPTID},
                DatabaseTable.Item.NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null,
                null,
                null
        );

        List<String> receiptIds = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                receiptIds.add(cursor.getString(0));
            }
        }

        cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                new String[] {DatabaseTable.Receipt.ID},
                DatabaseTable.Receipt.BIZ_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                receiptIds.add(cursor.getString(0));
            }
        }

        String ids = "";
        for(String id : receiptIds) {
            ids += "'" + id + "',";
        }

        if(ids.length() > 0) {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "select " +
                            "* " +
                            "from " + DatabaseTable.Receipt.TABLE_NAME + " " +
                            "where " + DatabaseTable.Receipt.ID + " IN (" + ids.substring(0, ids.length() - 1) + ")", null);

            List<ReceiptModel> receiptModels = retrieveReceiptModelFromCursor(cursor);
        }

        return filterByBizByMonth("Costco", new Date());
    }

    private static List<ReceiptModel> retrieveReceiptModelFromCursor(Cursor cursor) {
        List<ReceiptModel> list = new LinkedList<>();

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ReceiptModel receiptModel = new ReceiptModel();
                receiptModel.setBizName(cursor.getString(0));
                receiptModel.setAddress(cursor.getString(1));
                receiptModel.setPhone(cursor.getString(2));
                receiptModel.setReceiptDate(cursor.getString(3));
                receiptModel.setExpenseReport(cursor.getString(4));
                receiptModel.setBlobIds(cursor.getString(5));
                receiptModel.setId(cursor.getString(6));
                receiptModel.setNotes(cursor.getString(7));
                receiptModel.setPtax(cursor.getDouble(8));
                receiptModel.setRid(cursor.getString(9));
                receiptModel.setTax(cursor.getDouble(10));
                receiptModel.setTotal(cursor.getDouble(11));

                receiptModel.setReceiptItems(ReceiptItemUtils.getItems(receiptModel.getId()));
                list.add(receiptModel);
            }
        }

        return list;
    }
}
