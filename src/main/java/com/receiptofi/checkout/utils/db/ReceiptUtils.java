package com.receiptofi.checkout.utils.db;

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
import com.receiptofi.checkout.models.ReceiptModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Map;

import static com.receiptofi.checkout.utils.db.KeyValueUtils.KEYS;
import static com.receiptofi.checkout.utils.db.KeyValueUtils.insertKeyValue;

public class ReceiptUtils {

    public static void getUnprocessedCount() {

        ExternalCall.doGet(API.UNPROCESSED_COUNT_API, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                Message msg = new Message();
                msg.what = HomeActivity.UPDATE;
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
                Map<String, Map<String, String>> map = JsonParseUtils.parseReceipt(body);
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
                        ArrayList<ReceiptModel> models = ResponseParser.getReceipts(body);
                        for (ReceiptModel model : models) {
                            model.save();
                        }
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

        String[] columns = new String[]{DatabaseTable.Receipt.BIZ_NAME, DatabaseTable.Receipt.DATE_R, DatabaseTable.Receipt.P_TAX, DatabaseTable.Receipt.TOTAL, DatabaseTable.Receipt.ID, DatabaseTable.Receipt.FILES_BLOB};
        Cursor receiptsRecords = ReceiptofiApplication.RDH.getReadableDatabase().query(DatabaseTable.Receipt.TABLE_NAME, columns, null, null, null, null, null);

        ArrayList<ReceiptModel> rModels = new ArrayList<>();
        if (receiptsRecords != null && receiptsRecords.getCount() > 0) {
            for (receiptsRecords.moveToFirst(); !receiptsRecords.isAfterLast(); receiptsRecords.moveToNext()) {
                ReceiptModel model = new ReceiptModel();
                model.bizName = receiptsRecords.getString(0);
                model.date = receiptsRecords.getString(1);
                model.ptax = receiptsRecords.getDouble(2);
                model.total = receiptsRecords.getDouble(3);
                model.id = receiptsRecords.getString(4);
                model.filesBlobId = receiptsRecords.getString(5);
                rModels.add(model);
            }

        }
        if (null != receiptsRecords) {
            receiptsRecords.close();
        }
        return rModels;

    }

}
