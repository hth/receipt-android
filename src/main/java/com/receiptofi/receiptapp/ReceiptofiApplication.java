package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.db.DatabaseHandler;
import com.receiptofi.receiptapp.model.ProfileModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.db.DBUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.MonthlyReportUtils;
import com.receiptofi.receiptapp.utils.db.ProfileUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import io.fabric.sdk.android.Fabric;

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
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
        Iconify.with(new FontAwesomeModule());

        RDH = DatabaseHandler.getsInstance(this);
        if (KeyValueUtils.doesTableExists() &&
                TextUtils.isEmpty(UserUtils.getAuth()) &&
                TextUtils.isEmpty(UserUtils.getEmail())) {
            Log.d(TAG, "Found authId empty, re-set data");
            DBUtils.initializeDefaults();
        } else {
            MonthlyReportUtils.fetchMonthly();
            logUser();
        }
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }

    private void logUser() {
        ProfileModel profileModel = ProfileUtils.getProfile();
        Crashlytics.setUserIdentifier(UserUtils.getDeviceId());
        Crashlytics.setUserEmail(profileModel.getMail());
        Crashlytics.setUserName(profileModel.getRid());
    }
}
