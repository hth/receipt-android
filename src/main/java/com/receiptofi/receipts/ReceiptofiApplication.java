package com.receiptofi.receipts;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.receipts.adapters.ImageUpload;
import com.receiptofi.receipts.db.DatabaseHandler;
import com.receiptofi.receipts.utils.AppUtils;
import com.receiptofi.receipts.utils.UserUtils;
import com.receiptofi.receipts.utils.db.DBUtils;
import com.receiptofi.receipts.utils.db.KeyValueUtils;
import com.receiptofi.receipts.utils.db.MonthlyReportUtils;

import net.danlew.android.joda.JodaTimeAndroid;

public class ReceiptofiApplication extends Application {

    private static final String TAG = ReceiptofiApplication.class.getSimpleName();

    public static DatabaseHandler RDH;
    private static boolean homeActivityVisible;

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

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
