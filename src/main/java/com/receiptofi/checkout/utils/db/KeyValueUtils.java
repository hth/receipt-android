package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.http.API;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

public class KeyValueUtils {

    ReceiptofiApplication application;

    public static boolean insertKeyValue(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.KeyValue.KEY, key);
        values.put(DatabaseTable.KeyValue.VALUE, value);

        if (RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                values,
                DatabaseTable.KeyValue.KEY + "= ?",
                new String[]{key}
        ) <= 0) {
            long code = RDH.getWritableDatabase().insert(
                    DatabaseTable.KeyValue.TABLE_NAME,
                    null,
                    values
            );

            return code != -1;
        } else {
            return true;
        }
    }

    public static boolean deleteKey(String key) {
        return RDH.getWritableDatabase().delete(
                DatabaseTable.KeyValue.TABLE_NAME,
                DatabaseTable.KeyValue.KEY + " = ?",
                new String[]{key}
        ) > 0;
    }

    public static boolean updateValuesForKeyWithBlank(String key) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.KeyValue.VALUE, "");

        return RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                contentValues,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;
    }

    public static String getValue(String key) {
        Cursor c = RDH.getReadableDatabase().rawQuery(
                "select * from " + DatabaseTable.KeyValue.TABLE_NAME + " where " + DatabaseTable.KeyValue.KEY + "=" + "'" + key + "'",
                null
        );

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(DatabaseTable.KeyValue.VALUE));
        } else {
            return null;
        }
    }

    public static void clearKeyValues() {
        RDH.getReadableDatabase().delete(
                DatabaseTable.KeyValue.TABLE_NAME,
                null,
                null
        );
    }

    public static void clearReceiptsDB() {
        RDH.getReadableDatabase().delete(
                DatabaseTable.Receipt.TABLE_NAME,
                null,
                null
        );
    }

    public static class KEYS {
        public static String XR_MAIL = API.key.XR_MAIL;
        public static String XR_AUTH = API.key.XR_AUTH;
        public static String WIFI_SYNC = "WIFI_SYNC";
        public static String UNPROCESSED_DOCUMENT = "UNPROCESSED_DOCUMENT";
        public static String SOCIAL_LOGIN = "SOCIAL_LOGIN";
    }
}
