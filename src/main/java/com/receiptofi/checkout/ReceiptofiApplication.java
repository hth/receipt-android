package com.receiptofi.checkout;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.ReceiptofiDatabaseHandler;
import com.receiptofi.checkout.models.ReceiptDB;
import com.receiptofi.checkout.utils.AppUtils;

public class ReceiptofiApplication extends Application {

    public static ReceiptofiDatabaseHandler rdh;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        rdh = new ReceiptofiDatabaseHandler(this, ReceiptDB.DB_NAME);
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }



}
