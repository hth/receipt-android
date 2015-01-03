package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Message;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.Protocol;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.receiptofi.checkout.utils.db.KeyValueUtils.KEYS;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.insertKeyValue;

public class ReceiptUtils {

    private static final String TAG = ReceiptUtils.class.getSimpleName();

    public static void getUnprocessedCount() {

        ExternalCall.doGet(API.UNPROCESSED_COUNT_API, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                Message msg = new Message();
                msg.what = HomeActivity.UPDATE_UNPROCESSED_COUNT;
                Map<String, String> map = JsonParseUtils.parseUnprocessedCount(body);
                msg.obj = map.get(API.key.UNPROCESSEDCOUNT);
                insertKeyValue(KEYS.UNPROCESSED_DOCUMENT, map.get(API.key.UNPROCESSEDCOUNT));
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
                insertReceipts(JsonParseUtils.parseReceipt(body));
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
    private static void insertReceipts(List<ReceiptModel> receipts) {
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
        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                values
        );
    }
}
