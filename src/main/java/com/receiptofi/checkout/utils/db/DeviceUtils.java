package com.receiptofi.checkout.utils.db;

import android.os.Message;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by hitender on 1/4/15.
 */
public class DeviceUtils {

    /**
     * This method does the registration of the device when its not registered.
     */
    public static void getAllUpdates() {
        ExternalCall.doGet(true, API.ALL_DEVICE_UPDATE, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                Message msg = new Message();
                msg.what = HomeActivity.GET_ALL_RECEIPTS;
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                }

                MonthlyReportUtils.computeMonthlyReceiptReport();
            }

            @Override
            public void onError(int statusCode, String error) {

            }

            @Override
            public void onException(Exception exception) {

            }
        });
    }

}
