package com.receiptofi.android;

import com.google.android.gms.common.api.GoogleApiClient;

import com.receiptofi.android.adapters.ImageUpload;
import com.receiptofi.android.db.ReceiptofiDatabaseHandler;
import com.receiptofi.android.models.ReceiptDB;
import com.receiptofi.android.utils.AppUtils;

import android.app.Application;

public class ReceiptofiApplication extends Application {

    public static ReceiptofiDatabaseHandler rdh;
    GoogleApiClient googleApiClient;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        rdh = new ReceiptofiDatabaseHandler(this, ReceiptDB.DB_NAME);
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient(GoogleApiClient googleApiClient) {
        return this.googleApiClient;
    }


}
