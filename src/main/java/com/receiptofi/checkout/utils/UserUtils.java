package com.receiptofi.checkout.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
            String s = KeyValueUtils.getValue(KeyValueUtils.KEYS.WIFI_SYNC);
            if (s == null || s.equalsIgnoreCase("true")) {
                return true;
            } else {
                return false;
            }
        }

        public static void setWifiSync(Context context, boolean value) {
            Log.d(TAG, "saving wifi sync only to: " + value);
            KeyValueUtils.insertKeyValue(KeyValueUtils.KEYS.WIFI_SYNC, String.valueOf(value));
        }

        public static boolean isStartImageUploadProcess(Context context) {
            if ((UserUtils.UserSettings.isWifiSyncOnly() && AppUtils.isWifiConnected(context)) || (!UserUtils.UserSettings.isWifiSyncOnly() && ((AppUtils.isMobileInternetConnected(context) || AppUtils.isWifiConnected(context))))) {
                return true;
            } else {
                return false;
            }
        }
    }
}
