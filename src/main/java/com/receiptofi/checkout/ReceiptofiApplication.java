package com.receiptofi.checkout;

import android.app.Application;
import android.util.Log;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.ReceiptofiDatabaseHandler;
import com.receiptofi.checkout.db.ReceiptDB;
import com.receiptofi.checkout.utils.AppUtils;

public class ReceiptofiApplication extends Application {

    private static final String TAG = ReceiptofiApplication.class.getSimpleName();

    public static ReceiptofiDatabaseHandler RDH;
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
        Log.d(TAG, "executing onCreate");
        // TODO Auto-generated method stub
        super.onCreate();
        RDH = new ReceiptofiDatabaseHandler(this, ReceiptDB.DB_NAME);
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }

}
