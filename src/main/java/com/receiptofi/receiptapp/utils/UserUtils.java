package com.receiptofi.receiptapp.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.service.ImageUploaderService;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UserUtils {
    private static final String TAG = UserUtils.class.getSimpleName();

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    public static String getToken() {
        return KeyValueUtils.getValue(KeyValueUtils.KEYS.XR_TK);
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
        return !TextUtils.isEmpty(email) && email.equalsIgnoreCase(getEmail());
    }

    public static boolean isValidAppUser() {
        return !(TextUtils.isEmpty(UserUtils.getEmail()) || TextUtils.isEmpty(UserUtils.getAuth()));
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
                ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).homeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);

            }
        }

        public static boolean isStartImageUploadProcess(Context context) {
            boolean wifiConnected = AppUtils.isWifiConnected(context);
            if (isWifiSyncOnly() && wifiConnected) {
                Log.d(TAG, "isWifiSyncOnly: " + true + " and wifi is connected: " + wifiConnected);
                return true;
            } else if (!isWifiSyncOnly() && AppUtils.isNetworkConnectedOrConnecting(context)) {
                Log.d(TAG, "isWifiSyncOnly: " + false + " and wifi/mobile is connected: " + true);
                return true;
            } else {
                Log.d(TAG, "isStartImageUploadProcess returning: " + false);
                return false;
            }
        }
    }
}
