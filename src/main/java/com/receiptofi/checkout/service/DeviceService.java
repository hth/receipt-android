package com.receiptofi.checkout.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.fragments.ExpenseTagFragment;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.model.wrapper.DataWrapper;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.BillingAccountUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;
import com.receiptofi.checkout.utils.db.NotificationUtils;
import com.receiptofi.checkout.utils.db.ProfileUtils;
import com.receiptofi.checkout.utils.db.ReceiptItemUtils;
import com.receiptofi.checkout.utils.db.ReceiptUtils;
import com.squareup.okhttp.Headers;

import junit.framework.Assert;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * User: hitender
 * Date: 1/4/15 6:44 AM
 */
public class DeviceService {
    private static final String TAG = DeviceService.class.getSimpleName();

    private DeviceService() {
    }

    /**
     * Gets updates for this device.
     *
     * @param context
     */
    public static void getUpdates(final Context context) {
        Log.d(TAG, "get new update for device");
        ExternalCallWithOkHttp.doGet(context, IncludeDevice.YES, API.NEW_UPDATE_FOR_DEVICE, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                DeviceService.onSuccess(headers, body);
                showMessage("Syncing complete with latest data.", (Activity) context);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                if (null != context) {
                    showMessage(JsonParseUtils.parseError(error), (Activity) context);
                }
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                if (null != context) {
                    showMessage(e.getMessage(), (Activity) context);
                }
            }
        });
    }

    /**
     * Get all data for the user.
     *
     * @param context
     */
    public static void getAll(final Context context) {
        Log.d(TAG, "get all data for new device");
        ExternalCallWithOkHttp.doGet(context, API.ALL_FROM_BEGINNING, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                DeviceService.onSuccess(headers, body);

                /** Update DB if successful in getting all the data and update the DB. */
                KeyValueUtils.updateInsert(KeyValueUtils.KEYS.GET_ALL_COMPLETE, Boolean.toString(true));
                showMessage("Synced all data.", (Activity) context);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                if (null != context) {
                    showMessage(JsonParseUtils.parseError(error), (Activity) context);
                }
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                if (null != context) {
                    showMessage(e.getMessage(), (Activity) context);
                }
            }
        });
    }

    /**
     * Create new device identity and insert to database. Then register the device id. In case of failure, remove the
     * the device id.
     */
    public static void registerDevice(final Context context) {
        Log.d(TAG, "register device");
        KeyValueUtils.updateInsert(KeyValueUtils.KEYS.XR_DID, UUID.randomUUID().toString());

        ExternalCallWithOkHttp.doPost(context, API.REGISTER_DEVICE, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                boolean registration = JsonParseUtils.parseDeviceRegistration(body);
                if (!registration) {
                    Log.d(TAG, "Registration of device failed");
                    KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
                }
            }

            @Override
            public void onError(int statusCode, String error) {
                KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
                Log.e(TAG, "error=" + error);
                if (null != context) {
                    showMessage(JsonParseUtils.parseError(error), (Activity) context);
                }
            }

            @Override
            public void onException(Exception e) {
                KeyValueUtils.deleteKey(KeyValueUtils.KEYS.XR_DID);
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                if (null != context) {
                    showMessage(e.getMessage(), (Activity) context);
                }
            }
        });
    }

    public static void onSuccess(Headers headers, String body) {
        MainMaterialDrawerActivity mainMaterialDrawer = (MainMaterialDrawerActivity) AppUtils.getHomePageContext();
        Boolean refreshView = false;
        // If HomePageContext has been cleared, then we should discard the http response.
        if (AppUtils.getHomePageContext() == null) {
            return;
        }
        DataWrapper dataWrapper = JsonParseUtils.parseData(body);

        if (null != dataWrapper.getProfileModel()) {
            ProfileUtils.insert(dataWrapper.getProfileModel());
        }

        ReceiptUtils.insert(dataWrapper.getReceiptModels());
        ReceiptItemUtils.insert(dataWrapper.getReceiptItemModels());

        /** Insert or Delete Expense Tag. Note: Always return all the expense tag. */
        if (!dataWrapper.getExpenseTagModels().isEmpty()) {
            refreshView = ExpenseTagUtils.insert(dataWrapper.getExpenseTagModels());
            if (null != mainMaterialDrawer.expenseTagFragment) {
                mainMaterialDrawer.expenseTagFragment.updateHandler.sendEmptyMessage(ExpenseTagFragment.EXPENSE_TAG_UPDATED);
            }
        }

        NotificationUtils.insert(dataWrapper.getNotificationModels());
        if (null != dataWrapper.getBillingAccountModel()) {
            BillingAccountUtils.insertOrReplace(dataWrapper.getBillingAccountModel());
        }

        KeyValueUtils.updateInsert(
                KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT,
                dataWrapper.getUnprocessedDocumentModel().getCount());

        Message countMessage = new Message();
        countMessage.obj = dataWrapper.getUnprocessedDocumentModel().getCount();
        countMessage.what = HomeFragment.UPDATE_UNPROCESSED_COUNT;
        if (ReceiptofiApplication.isHomeActivityVisible()) {
            mainMaterialDrawer.homeFragment.updateHandler.sendMessage(countMessage);
        }

        if (!dataWrapper.getReceiptModels().isEmpty()) {
            MonthlyReportUtils.computeMonthlyReceiptReport();
            String[] monthDay = HomeFragment.DF_YYYY_MM.format(new Date()).split(Pattern.quote(" "));
            Message amountMessage = new Message();
            amountMessage.obj = MonthlyReportUtils.fetchMonthlyTotal(monthDay[0], monthDay[1]);
            amountMessage.what = HomeFragment.UPDATE_MONTHLY_EXPENSE;
            if (ReceiptofiApplication.isHomeActivityVisible()) {
                mainMaterialDrawer.homeFragment.updateHandler.sendMessage(amountMessage);
                mainMaterialDrawer.homeFragment.updateHandler.sendEmptyMessage(HomeFragment.UPDATE_EXP_BY_BIZ_CHART);
            }

            refreshView = true;
        }

        if (refreshView) {
            /** Populate data for detail views. */
            MonthlyReportUtils.fetchMonthly();
        }
    }

    /**
     * Show Toast message.
     *
     * @param message
     * @param context
     */
    private static void showMessage(final String message, final Activity context) {
        Assert.assertNotNull("Context should not be null", context);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperActivityToast superActivityToast = new SuperActivityToast(context);
                superActivityToast.setText(message);
                superActivityToast.setDuration(SuperToast.Duration.SHORT);
                superActivityToast.setBackground(SuperToast.Background.BLUE);
                superActivityToast.setTextColor(Color.WHITE);
                superActivityToast.setTouchToDismiss(true);
                superActivityToast.show();
            }
        });
    }
}
