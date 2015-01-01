package com.receiptofi.checkout.utils;

import android.database.Cursor;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.HTTPEndpoints;
import com.receiptofi.checkout.http.HTTPUtils;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.models.ReceiptDB;
import com.receiptofi.checkout.models.ReceiptModel;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class ReceiptUtils {

    public static void updateAmountAndCount(){


    }

    public static void fetchReceiptsAndSave() {

        ArrayList<NameValuePair> headerData = new ArrayList<NameValuePair>();
        headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
        headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));

        HTTPUtils.AsyncRequest(headerData, API.GET_ALL_RECEIPTS,
                HTTPEndpoints.HTTP_METHOD_GET, new ResponseHandler() {

                    @Override
                    public void onSuccess(Header[] arr) {
                        ArrayList<ReceiptModel> models = null; //ResponseParser.getReceipts(response);
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
        ReceiptofiApplication.RDH.getWritableDatabase().rawQuery("delete from " + ReceiptDB.Receipt.TABLE_NAME + ";", null);
    }

    public static ArrayList<ReceiptModel> getAllReciepts() {

        String[] coloumns = new String[]{ReceiptDB.Receipt.BIZ_NAME, ReceiptDB.Receipt.DATE_R, ReceiptDB.Receipt.P_TAX, ReceiptDB.Receipt.TOTAL, ReceiptDB.Receipt.ID, ReceiptDB.Receipt.FILES_BLOB};
        Cursor recieptsRecords = ReceiptofiApplication.RDH.getReadableDatabase().query(ReceiptDB.Receipt.TABLE_NAME, coloumns, null, null, null, null, null);

        ArrayList<ReceiptModel> rModels = new ArrayList<ReceiptModel>();
        if (recieptsRecords != null && recieptsRecords.getCount() > 0) {
            for (recieptsRecords.moveToFirst(); !recieptsRecords.isAfterLast(); recieptsRecords.moveToNext()) {
                ReceiptModel model = new ReceiptModel();
                model.bizName = recieptsRecords.getString(0);
                model.date = recieptsRecords.getString(1);
                model.ptax = recieptsRecords.getDouble(2);
                model.total = recieptsRecords.getDouble(3);
                model.id = recieptsRecords.getString(4);
                model.filesBlobId = recieptsRecords.getString(5);
                rModels.add(model);
            }

        }
        recieptsRecords.close();
        recieptsRecords = null;
        return rModels;

    }

}
