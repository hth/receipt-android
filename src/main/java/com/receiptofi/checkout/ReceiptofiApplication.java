package com.receiptofi.checkout;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.DatabaseHandler;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.DBUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.utils.db.MonthlyReportUtils;

import net.danlew.android.joda.JodaTimeAndroid;

public class ReceiptofiApplication extends Application {

    private static final String TAG = ReceiptofiApplication.class.getSimpleName();

    public static DatabaseHandler RDH;
    private static boolean homeActivityVisible;

    public static boolean isHomeActivityVisible() {
        return homeActivityVisible;
    }

    public static void homeActivityResumed() {
        homeActivityVisible = true;
    }

    public static void homeActivityPaused() {
        homeActivityVisible = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ReceiptofiApplication onCreate");

        JodaTimeAndroid.init(this);
        RDH = DatabaseHandler.getsInstance(this);
        if (KeyValueUtils.doesTableExists() &&
                TextUtils.isEmpty(UserUtils.getAuth()) &&
                TextUtils.isEmpty(UserUtils.getEmail())) {
            Log.d(TAG, "Found authId empty, re-set data");
            DBUtils.initializeDefaults();
        } else {
            MonthlyReportUtils.fetchMonthly();
        }
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }

}
