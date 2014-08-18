package com.receiptofi.android;

import android.app.Application;

import com.receiptofi.android.db.ReceiptofiDatabaseHandler;
import com.receiptofi.android.models.ReceiptDB;

public class ReceiptofiApplication extends Application {

	ReceiptofiDatabaseHandler receiptofiDatabaseHandler;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		receiptofiDatabaseHandler = new ReceiptofiDatabaseHandler(this,
				ReceiptDB.DB_NAME);
	}
}
