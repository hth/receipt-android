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

	public static boolean insertKeyValue(Context context, String key, String value) {
		ContentValues values = new ContentValues();
		values.put(ReceiptDB.KeyVal.KEY, key);
		values.put(ReceiptDB.KeyVal.VALUE, value);

		if (ReceiptofiApplication.rdh.getWritableDatabase().update(
                ReceiptDB.KeyVal.TABLE_NAME,
                values,
                ReceiptDB.KeyVal.KEY + "= ?",
                new String[]{key}
        ) <= 0) {
            long code = ReceiptofiApplication.rdh.getWritableDatabase().insert(
                    ReceiptDB.KeyVal.TABLE_NAME,
                    null,
                    values
            );

            return code != -1;
		} else {
			return true;
		}
	}

	public static String getValue(String key) {
		Cursor c = ReceiptofiApplication.rdh.getReadableDatabase().rawQuery(
                "select * from " + ReceiptDB.KeyVal.TABLE_NAME + " where " + ReceiptDB.KeyVal.KEY + "=" + "'" + key + "'",
                null
        );

		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			return c.getString(c.getColumnIndex(ReceiptDB.KeyVal.VALUE));
		} else {
			return null;
		}
	}
	
	public static void clearKeyValues() {
		 ReceiptofiApplication.rdh.getReadableDatabase().delete(
                 ReceiptDB.KeyVal.TABLE_NAME,
                 null,
                 null
         );
	}
}
