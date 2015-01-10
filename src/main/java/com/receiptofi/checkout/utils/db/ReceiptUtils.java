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
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.UnprocessedDocumentModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.KEYS;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.updateInsert;

public class ReceiptUtils {

    private static final String TAG = ReceiptUtils.class.getSimpleName();

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
                insertReceipts(JsonParseUtils.parseReceipts(body));
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
                        insertReceipts(ResponseParser.getReceipts(body));
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

        String[] columns = new String[]{DatabaseTable.Receipt.BIZ_NAME, DatabaseTable.Receipt.DATE, DatabaseTable.Receipt.PTAX, DatabaseTable.Receipt.TOTAL, DatabaseTable.Receipt.ID, DatabaseTable.Receipt.BLOB_IDS};
        Cursor receiptsRecords = ReceiptofiApplication.RDH.getReadableDatabase().query(DatabaseTable.Receipt.TABLE_NAME, columns, null, null, null, null, null);

        ArrayList<ReceiptModel> rModels = new ArrayList<>();
        if (receiptsRecords != null && receiptsRecords.getCount() > 0) {
            for (receiptsRecords.moveToFirst(); !receiptsRecords.isAfterLast(); receiptsRecords.moveToNext()) {
                ReceiptModel model = new ReceiptModel();
                model.setBizName(receiptsRecords.getString(0));
                model.setDate(receiptsRecords.getString(1));
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
    public static void insertReceipts(List<ReceiptModel> receipts) {
        for (ReceiptModel receipt : receipts) {
            insertReceipt(receipt);
        }
    }

    /**
     * Insert receipt in table.
     *
     * @param receipt
     */
    private static void insertReceipt(ReceiptModel receipt) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Receipt.BIZ_NAME, receipt.getBizName());
        values.put(DatabaseTable.Receipt.BIZ_STORE_ADDRESS, receipt.getAddress());
        values.put(DatabaseTable.Receipt.BIZ_STORE_PHONE, receipt.getPhone());
        values.put(DatabaseTable.Receipt.DATE, receipt.getDate());
        values.put(DatabaseTable.Receipt.EXPENSE_REPORT, receipt.getExpenseReport());
        values.put(DatabaseTable.Receipt.BLOB_IDS, receipt.getBlobIds());
        values.put(DatabaseTable.Receipt.ID, receipt.getId());
        values.put(DatabaseTable.Receipt.NOTES, receipt.getNotes());
        values.put(DatabaseTable.Receipt.PTAX, receipt.getPtax());
        values.put(DatabaseTable.Receipt.RID, receipt.getRid());
        values.put(DatabaseTable.Receipt.TOTAL, receipt.getTotal());

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
                        "where date between " +
                        "datetime('now', 'start of month') AND " +
                        "datetime('now', 'start of month','+1 month','-1 day') " +
                        " group by bizName", null);

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

        List<ReceiptModel> list = new LinkedList<>();
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                "SUBSTR(date, 6, 2) = ? and SUBSTR(date, 1, 4) = ? ",
                new String[]{month, year},
                null,
                null,
                DatabaseTable.Receipt.DATE + " desc"
        );

        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ReceiptModel receiptModel = new ReceiptModel();
                receiptModel.setBizName(cursor.getString(0));
                receiptModel.setAddress(cursor.getString(1));
                receiptModel.setPhone(cursor.getString(2));
                receiptModel.setDate(cursor.getString(3));
                receiptModel.setExpenseReport(cursor.getString(4));
                receiptModel.setBlobIds(cursor.getString(5));
                receiptModel.setId(cursor.getString(6));
                receiptModel.setNotes(cursor.getString(7));
                receiptModel.setPtax(cursor.getDouble(8));
                receiptModel.setRid(cursor.getString(9));
                receiptModel.setTotal(cursor.getDouble(10));

                receiptModel.setReceiptItems(ReceiptItemUtils.getItems(receiptModel.getId()));
                list.add(receiptModel);
            }
        }
        return list;
    }
}
