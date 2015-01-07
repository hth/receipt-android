package com.receiptofi.checkout.utils.db;

import android.os.Message;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;

import org.apache.http.Header;

import java.util.UUID;

/**
 * Created by hitender on 1/4/15.
 */
public class DeviceUtils {

    public static void getNewUpdates() {
        ExternalCall.doGet(IncludeDevice.YES, API.NEW_UPDATE_FOR_DEVICE, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceUtils.onSuccess(headers, body);
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
        ExternalCall.doGet(IncludeDevice.NO, API.ALL_FROM_BEGINNING, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceUtils.onSuccess(headers, body);
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
        KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.XR_DID, UUID.randomUUID().toString());

        ExternalCall.doPost(API.REGISTER_DEVICE, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers, String body) {
                boolean registration = JsonParseUtils.parseDeviceRegistration(body);
                if (!registration) {
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
        Message msg = new Message();
        msg.obj = "1";
        msg.what = HomeActivity.UPDATE_UNPROCESSED_COUNT;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
        }

        MonthlyReportUtils.computeMonthlyReceiptReport();

        msg.obj = "1";
        msg.what = HomeActivity.UPDATE_MONTHLY_EXPENSE;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
        }
    }
}
