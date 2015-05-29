package com.receiptofi.checkout.service;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.MainPageActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.fragments.TagModifyFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.DataWrapper;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.BillingAccountUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;
import com.receiptofi.checkout.utils.db.NotificationUtils;
import com.receiptofi.checkout.utils.db.ReceiptItemUtils;
import com.receiptofi.checkout.utils.db.ReceiptUtils;

import org.apache.http.Header;

import java.util.Date;
import java.util.UUID;

/**
 * User: hitender
 * Date: 1/4/15 6:44 AM
 */
public class DeviceService {
    private static final String TAG = DeviceService.class.getSimpleName();

    private DeviceService() {
    }

    public static void getNewUpdates(Context context) {
        Log.d(TAG, "get new update for device");
        ExternalCall.doGet(context, IncludeDevice.YES, API.NEW_UPDATE_FOR_DEVICE, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceService.onSuccess(headers, body);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing getNewUpdates: onError: " + error);
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing getNewUpdates: onException: " + exception.getMessage());
            }
        });
    }

    public static void getAll(Context context) {
        Log.d(TAG, "get all data for new device");
        ExternalCall.doGet(context, IncludeDevice.NO, API.ALL_FROM_BEGINNING, new ResponseHandler() {
            @Override
            public void onSuccess(Header[] headers, String body) {
                DeviceService.onSuccess(headers, body);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing getAll: onError: " + error);
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing getAll: onException: " + exception.getMessage());
            }
        });
    }

    /**
     * Create new device identity and insert to database. Then register the device id. In case of failure, remove the
     * the device id.
     */
    public static void registerDevice(Context context) {
        Log.d(TAG, "register device");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_DID, UUID.randomUUID().toString());

        ExternalCall.doPost(context, API.REGISTER_DEVICE, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {

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
                Log.d(TAG, "executing registerDevice: onError: " + error);
            }

            @Override
            public void onException(Exception exception) {
                KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
                Log.d(TAG, "executing registerDevice: onException: " + exception.getMessage());
            }
        });
    }

    public static void onSuccess(Header[] headers, String body) {
        DataWrapper dataWrapper = JsonParseUtils.parseData(body);
        ReceiptUtils.insert(dataWrapper.getReceiptModels());
        ReceiptItemUtils.insert(dataWrapper.getReceiptItemModels());

        /** Insert or Delete Expense Tag. Note: Always return all the expense tag. */
        if (!dataWrapper.getExpenseTagModels().isEmpty()) {
            ExpenseTagUtils.deleteAll();
            ExpenseTagUtils.insert(dataWrapper.getExpenseTagModels());
            // KEVIN : Add below solution for new tag modify page.
            if (null != ((MainPageActivity) AppUtils.getHomePageContext()).mTagModifyFragment) {
                ((MainPageActivity) AppUtils.getHomePageContext()).mTagModifyFragment.updateHandler.sendEmptyMessage(TagModifyFragment.EXPENSE_TAG_UPDATED);
            }
        } else {
            ExpenseTagUtils.deleteAll();
        }

        NotificationUtils.insert(dataWrapper.getNotificationModels());
        if (null != dataWrapper.getBillingAccountModel()) {
            BillingAccountUtils.insertOrReplace(dataWrapper.getBillingAccountModel());
        }

        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, dataWrapper.getUnprocessedDocumentModel().getCount());

        Message countMessage = new Message();
        countMessage.obj = dataWrapper.getUnprocessedDocumentModel().getCount();
//        countMessage.what = HomeActivity.UPDATE_UNPROCESSED_COUNT;
        // KEVIN : Add for new setting.
        countMessage.what = HomeFragment.UPDATE_UNPROCESSED_COUNT;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
//            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(countMessage);
            // KEVIN : Add for new setting.
//            HomeFragment.newInstance("", "").updateHandler.sendMessage(countMessage);
            ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendMessage(countMessage);
        }

        if (!dataWrapper.getReceiptModels().isEmpty()) {
            MonthlyReportUtils.computeMonthlyReceiptReport();

//            String[] monthDay = HomeActivity.DF_YYYY_MM.format(new Date()).split(" ");
            // KEVIN : Replace the DF_YYYY_MM with HomeFragment
            String[] monthDay = HomeFragment.DF_YYYY_MM.format(new Date()).split(" ");
            Message amountMessage = new Message();
            amountMessage.obj = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
//            amountMessage.what = HomeActivity.UPDATE_MONTHLY_EXPENSE;
            // KEVIN : Add for new setting page.
            amountMessage.what = HomeFragment.UPDATE_MONTHLY_EXPENSE;
            if (ReceiptofiApplication.isHomeActivityVisible()) {
//                ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(amountMessage);
//                ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendEmptyMessage(HomeActivity.UPDATE_EXP_BY_BIZ_CHART);
                // KEVIN : add for new setting page.
//                HomeFragment.newInstance("", "").updateHandler.sendMessage(amountMessage);
//                HomeFragment.newInstance("", "").updateHandler.sendEmptyMessage(HomeFragment.UPDATE_EXP_BY_BIZ_CHART);

                ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendMessage(amountMessage);
                ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendEmptyMessage(HomeFragment.UPDATE_EXP_BY_BIZ_CHART);

            }

            /** Populate data in advance for master/detail views */
            MonthlyReportUtils.fetchMonthly();
        }
    }
}
