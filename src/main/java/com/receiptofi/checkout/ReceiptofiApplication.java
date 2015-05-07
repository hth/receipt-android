package com.receiptofi.checkout;

import android.app.Application;
import android.util.Log;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.DatabaseHandler;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.db.DBUtils;

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
        // TODO Auto-generated method stub
        super.onCreate();
        JodaTimeAndroid.init(this);
        Log.d(TAG, "ReceiptofiApplication onCreate");
        RDH = DatabaseHandler.getsInstance(this);
        DBUtils.initializeDefaults();
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }
}
