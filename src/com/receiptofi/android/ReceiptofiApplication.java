package com.receiptofi.android;

import android.app.Application;

import com.receiptofi.android.adapters.ImageUpload;
import com.receiptofi.android.db.ReceiptofiDatabaseHandler;
import com.receiptofi.android.models.ImageModel;
import com.receiptofi.android.models.ReceiptDB;
import com.receiptofi.android.utils.AppUtils;

public class ReceiptofiApplication extends Application {

	public static ReceiptofiDatabaseHandler rdh;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		rdh = new ReceiptofiDatabaseHandler(this, ReceiptDB.DB_NAME);
		ImageUpload.initializeQueue();
		AppUtils.setHomePageContext(null);
	}
	
}
