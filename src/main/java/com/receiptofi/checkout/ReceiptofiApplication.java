package com.receiptofi.checkout;

import android.app.Application;
import android.util.Log;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.DatabaseHandler;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.db.DBUtils;

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
        Log.d(TAG, "executing onCreate");
        RDH = new DatabaseHandler(this, DatabaseTable.DB_NAME);
        DBUtils.initializeDefaults();
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }

}
