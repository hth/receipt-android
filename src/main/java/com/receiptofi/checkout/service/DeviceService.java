package com.receiptofi.checkout.service;

import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.DataWrapper;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;
import com.receiptofi.checkout.utils.db.ReceiptItemUtils;
import com.receiptofi.checkout.utils.db.ReceiptUtils;

import org.apache.http.Header;

import java.util.Date;
import java.util.UUID;

/**
 * User: hitender
 * Date: 1/8/15 12:13 AM
 */
public class DeviceService {
    private static final String TAG = DeviceService.class.getSimpleName();

    public static void getNewUpdates() {
        Log.d(TAG, "get new update for device");
        ExternalCall.doGet(IncludeDevice.YES, API.NEW_UPDATE_FOR_DEVICE, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceService.onSuccess(headers, body);
            }

            @Override
            public void onError(int statusCode, String error) {

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }

    public static void getAll() {
        Log.d(TAG, "get all data for new device");
        ExternalCall.doGet(IncludeDevice.NO, API.ALL_FROM_BEGINNING, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceService.onSuccess(headers, body);
            }

            @Override
            public void onError(int statusCode, String error) {

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }

    /**
     * Create new device identity and insert to database. Then register the device id. In case of failure, remove the
     * the device id.
     */
    public static void registerDevice() {
        Log.d(TAG, "register device");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_DID, UUID.randomUUID().toString());

        ExternalCall.doPost(API.REGISTER_DEVICE, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers, String body) {
                boolean registration = JsonParseUtils.parseDeviceRegistration(body);
                if (!registration) {
                    Log.d(TAG, "register device failed");
                    KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
                }
            }

            @Override
            public void onError(int statusCode, String error) {
                KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
            }

            @Override
            public void onException(Exception exception) {
                KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
            }
        });
    }

    private static void onSuccess(Header[] headers, String body) {
        DataWrapper dataWrapper = JsonParseUtils.parseData(body);
        ReceiptUtils.insertReceipts(dataWrapper.getReceiptModels());
        ReceiptItemUtils.insertItems(dataWrapper.getReceiptItemModels());
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, dataWrapper.getUnprocessedDocumentModel().getCount());

        Message msg = new Message();
        msg.obj = dataWrapper.getUnprocessedDocumentModel().getCount();
        msg.what = HomeActivity.UPDATE_UNPROCESSED_COUNT;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
        }

        MonthlyReportUtils.computeMonthlyReceiptReport();

        //TODO not updating why
        String[] monthDay = HomeActivity.DF_YYYY_MM.format(new Date()).split(" ");
        msg.obj = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
        msg.what = HomeActivity.UPDATE_MONTHLY_EXPENSE;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
        }
    }
}