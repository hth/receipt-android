package com.receiptofi.checkout.utils;

import android.content.Context;
import android.util.Log;

import com.receiptofi.checkout.db.KeyValue;

public class UserUtils {

    private static final String TAG = "SUMAN"; //UserUtils.class.getSimpleName();

    static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isValidEmail(String email) {
        if (email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getEmail() {
        return KeyValue.getValue(KeyValue.key.XR_MAIL);
    }

    public static String getAuth() {
        return KeyValue.getValue(KeyValue.key.XR_AUTH);
    }

    public static boolean isValidAppUser() {
        String mail = UserUtils.getEmail();
        String auth = UserUtils.getAuth();
        Log.d(TAG, "mail is:  " + mail + "  auth is:  " + auth);

        if (mail != null && mail.trim().length() > 0 && auth != null && auth.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static class UserSettings {
        public static boolean isWifiSyncOnly() {
            String s = KeyValue.getValue(KeyValue.key.WIFI_SYNC);
            if (s == null || s.equalsIgnoreCase("true")) {
                return true;
            } else {
                return false;
            }
        }

        public static void setWifiSync(Context context, boolean value) {
            KeyValue.insertKeyValue(context, KeyValue.key.WIFI_SYNC, String.valueOf(value));
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
