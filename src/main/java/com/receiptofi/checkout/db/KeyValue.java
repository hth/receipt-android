package com.receiptofi.checkout.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.models.ReceiptDB;

import static com.receiptofi.checkout.ReceiptofiApplication.*;

public class KeyValue {

    public static class key {
        public static String XR_MAIL = "X-R-MAIL";
        public static String XR_AUTH = "X-R-AUTH";
        public static String WIFI_SYNC = "wifi_sync";
    }

    ReceiptofiApplication application;

    public static boolean insertKeyValue(Context context, String key, String value) {
        ContentValues values = new ContentValues();
        values.put(ReceiptDB.KeyVal.KEY, key);
        values.put(ReceiptDB.KeyVal.VALUE, value);

        if (RDH.getWritableDatabase().update(
                ReceiptDB.KeyVal.TABLE_NAME,
                values,
                ReceiptDB.KeyVal.KEY + "= ?",
                new String[]{key}
        ) <= 0) {
            long code = RDH.getWritableDatabase().insert(
                    ReceiptDB.KeyVal.TABLE_NAME,
                    null,
                    values
            );

            return code != -1;
        } else {
            return true;
        }
    }

    public static void deleteKey(String key) {
        RDH.getReadableDatabase().delete(
                ReceiptDB.KeyVal.TABLE_NAME,
                ReceiptDB.KeyVal.KEY + " = ?",
                new String[]{key}
        );
    }

    public static void removeValue(String key) {
        RDH.getReadableDatabase().insert(
                ReceiptDB.KeyVal.TABLE_NAME,
                key,
                null
        );
    }

    public static String getValue(String key) {
        Cursor c = RDH.getReadableDatabase().rawQuery(
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

    public static void logout() {

    }

    public static void clearKeyValues() {
        RDH.getReadableDatabase().delete(
                ReceiptDB.KeyVal.TABLE_NAME,
                null,
                null
        );
    }

    public static void clearReceiptsDB() {
        RDH.getReadableDatabase().delete(
                ReceiptDB.Receipt.TABLE_NAME,
                null,
                null
        );
    }
}

