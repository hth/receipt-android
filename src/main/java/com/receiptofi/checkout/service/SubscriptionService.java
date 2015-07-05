package com.receiptofi.checkout.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.model.wrapper.PlanWrapper;
import com.squareup.okhttp.Headers;

import junit.framework.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 7/4/15 9:14 PM
 */
public class SubscriptionService {
    private static final String TAG = DeviceService.class.getSimpleName();

    private static List<PlanModel> planModels = new LinkedList<>();

    private SubscriptionService() {
    }

    /**
     * @param context
     */
    public static void getPlans(final Context context) {
        ExternalCallWithOkHttp.doGet(context, API.PLANS_API, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                SubscriptionService.parsePlans(headers, body);
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

    public static void parsePlans(Headers headers, String body) {
        PlanWrapper planWrapper = JsonParseUtils.parsePlan(body);
    }

    public static List<PlanModel> getPlanModels() {
        return planModels;
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
