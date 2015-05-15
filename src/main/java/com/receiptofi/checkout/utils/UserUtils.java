package com.receiptofi.checkout.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.db.KeyValueUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UserUtils {

    private static final String TAG = UserUtils.class.getSimpleName();

    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static String getEmail() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_MAIL);
    }

    public static String getAuth() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_AUTH);
    }

    public static String getDeviceId() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_DID);
    }

    /**
     * Supports UTF-8 Encoding.
     *
     * @return
     */
    public static String getEmailEncoded() {
        try {
            return URLEncoder.encode(getEmail(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "email encoding : " + e.getLocalizedMessage(), e);
            return getEmail();
        }
    }

    /**
     * Supports UTF-8 Encoding.
     *
     * @return
     */
    public static String getAuthEncoded() {
        try {
            return URLEncoder.encode(getAuth(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "auth encoding : " + e.getLocalizedMessage(), e);
            return getAuth();
        }
    }

    public static boolean userExist(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return email.equalsIgnoreCase(getEmail());
    }

    public static boolean isValidAppUser() {
        if (TextUtils.isEmpty(UserUtils.getEmail()) || TextUtils.isEmpty(UserUtils.getAuth())) {
            return false;
        } else {
            return true;
        }
    }

    public static class UserSettings {

        public static boolean isWifiSyncOnly() {
            String str = KeyValueUtils.getValue(KeyValueUtils.KEYS.WIFI_SYNC);
            if (str == null || str.equalsIgnoreCase("true")) {
                Log.d(TAG, "isWifiSyncOnly: " + true);
                return true;
            } else {
                Log.d(TAG, "isWifiSyncOnly: " + false);
                return false;
            }
        }

        public static void setWifiSync(Context context, boolean value) {
            Log.d(TAG, "saving wifi sync only to: " + value);
            KeyValueUtils.updateInsert(KeyValueUtils.KEYS.WIFI_SYNC, String.valueOf(value));
            if (context != null && UserUtils.UserSettings.isStartImageUploadProcess(context)) {
                ImageUploaderService.start(context);
            } else {
                ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendEmptyMessage(HomeActivity.IMAGE_ADDED_TO_QUEUED);
            }
        }

        public static boolean isStartImageUploadProcess(Context context) {
            boolean wifiConnected = AppUtils.isWifiConnected(context);
            if(isWifiSyncOnly() && wifiConnected){
                Log.d(TAG, "isWifiSyncOnly: " + true + " and wifi is connected: " + wifiConnected);
                return true;
            } else if (!isWifiSyncOnly() && AppUtils.isNetworkConnected(context)) {
                Log.d(TAG, "isWifiSyncOnly: " + false + " and wifi/mobile is connected: " + true);
                return true;
            } else {
                Log.d(TAG, "isStartImageUploadProcess returning: " + false);
                return false;
            }
        }
    }
}
