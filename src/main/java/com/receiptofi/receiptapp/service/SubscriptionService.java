package com.receiptofi.receiptapp.service;

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
import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.SubscriptionUserActivity;
import com.receiptofi.receiptapp.fragments.SubscriptionFragment;
import com.receiptofi.receiptapp.fragments.SubscriptionUserFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.model.types.IncludeDevice;
import com.receiptofi.receiptapp.utils.JsonParseUtils;

import junit.framework.Assert;

import org.json.JSONObject;

import okhttp3.Headers;

/**
 * User: hitender
 * Date: 7/4/15 9:14 PM
 */
public class SubscriptionService {
    private static final String TAG = DeviceService.class.getSimpleName();

    private SubscriptionService() {
    }

    /**
     * Get all plans.
     *
     * @param context - Call in Activity's context
     */
    public static void getToken(final Context context) {
        Log.d(TAG, "Fetching token");

        ExternalCallWithOkHttp.doPost(context, API.TOKEN_API, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                JsonParseUtils.parseToken(body);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.TOKEN_SUCCESS;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.TOKEN_FAILURE;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);

                showMessage(JsonParseUtils.parseForErrorReason(error), (Activity) context);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.TOKEN_FAILURE;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);

                showMessage(e.getMessage(), (Activity) context);
            }
        });
    }

    /**
     * Get all plans.
     *
     * @param context - Call in Activity's context
     */
    public static void getPlans(final Context context) {
        ExternalCallWithOkHttp.doGet(context, API.PLANS_API, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                JsonParseUtils.parsePlan(body);

                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.PLAN_FETCH_SUCCESS;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.PLAN_FETCH_FAILURE;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);

                showMessage(JsonParseUtils.parseForErrorReason(error), (Activity) context);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionFragment.PLAN_FETCH_FAILURE;
                ((MainMaterialDrawerActivity) context).getSubscriptionFragment().updateHandler.dispatchMessage(message);

                showMessage(e.getMessage(), (Activity) context);
            }
        });
    }

    /**
     * Do the payment.
     *
     * @param context - Call in activity's context.
     */
    public static void doPayment(final Context context, final JSONObject jsonObject) {
        Log.d(TAG, "Do Payment " + jsonObject.toString());

        ExternalCallWithOkHttp.doPost(context, jsonObject, API.PAYMENT_API, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                JsonParseUtils.parseTransaction(body);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_PAYMENT_SUCCESS;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_PAYMENT_FAILED;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);

                showMessage(JsonParseUtils.parseForErrorReason(error), (Activity) context);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_PAYMENT_FAILED;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);

                showMessage(e.getMessage(), (Activity) context);
            }
        });
    }

    /**
     * Do the payment.
     *
     * @param context - Call in activity's context.
     */
    public static void cancelSubscription(final Context context) {
        Log.d(TAG, "Cancel subscription");

        ExternalCallWithOkHttp.doPost(context, API.CANCEL_SUBSCRIPTION_API, IncludeAuthentication.YES, IncludeDevice.YES, new ResponseHandler() {
            @Override
            public void onSuccess(Headers headers, String body) {
                JsonParseUtils.parseTransaction(body);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_CANCELLED_SUCCESS;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "error=" + error);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_CANCELLED_FAILED;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);

                showMessage(JsonParseUtils.parseForErrorReason(error), (Activity) context);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                Message message = new Message();
                message.obj = "";
                message.what = SubscriptionUserFragment.SUBSCRIPTION_CANCELLED_FAILED;
                ((SubscriptionUserActivity) context).subscriptionUserFragment.updateHandler.dispatchMessage(message);

                showMessage(e.getMessage(), (Activity) context);
            }
        });
    }

    /**
     * Show Toast message.
     *
     * @param message - Toast message
     * @param context - Show up activity's context
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
                superActivityToast.setBackground(SuperToast.Background.RED);
                superActivityToast.setTextColor(Color.WHITE);
                superActivityToast.setTouchToDismiss(true);
                superActivityToast.show();
            }
        });
    }
}
