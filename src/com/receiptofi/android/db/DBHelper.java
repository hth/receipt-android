package com.receiptofi.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.receiptofi.android.ReceiptofiApplication;
import com.receiptofi.android.models.ReceiptDB;

public class DBHelper {

	public static class key {
		public static String XR_MAIL = "X-R-MAIL";
		public static String XR_AUTH = "X-R-AUTH";
	}

	ReceiptofiApplication application;

	public static boolean insertKeyValue(Context context, String Key,
			String Value) {

		ContentValues values = new ContentValues();
		values.put(ReceiptDB.KeyVal.KEY, Key);
		values.put(ReceiptDB.KeyVal.VALUE, Value);

		if (ReceiptofiApplication.receiptofiDatabaseHandler
				.getWritableDatabase().update(ReceiptDB.KeyVal.TABLE_NAME,
						values, ReceiptDB.KeyVal.KEY +"= ?" , new String[]{Key}) <= 0) {
			long code = ReceiptofiApplication.receiptofiDatabaseHandler
					.getWritableDatabase().insert(ReceiptDB.KeyVal.TABLE_NAME,
							null, values);
			if (code != -1) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public static String getValue(String key) {
		Cursor c = ReceiptofiApplication.receiptofiDatabaseHandler
				.getReadableDatabase().rawQuery(
						"select * from " + ReceiptDB.KeyVal.TABLE_NAME
								+ " where " + ReceiptDB.KeyVal.KEY + "='" + key +"'",
						null);
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			String thevalue = c.getString(c
					.getColumnIndex(ReceiptDB.KeyVal.VALUE));
			c = null;
			return thevalue;
		} else {
			return null;
		}

	}
	
	public static void clearKeyValues() {
		 ReceiptofiApplication.receiptofiDatabaseHandler
			.getReadableDatabase().delete(ReceiptDB.KeyVal.TABLE_NAME, null, null);
	}
}
