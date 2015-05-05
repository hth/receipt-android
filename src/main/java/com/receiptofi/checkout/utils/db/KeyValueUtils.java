package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.http.API;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

public class KeyValueUtils {
    private static final String TAG = KeyValueUtils.class.getSimpleName();

    private KeyValueUtils() {
    }

    public static boolean updateInsert(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.KeyValue.KEY, key);
        values.put(DatabaseTable.KeyValue.VALUE, value);

        int update = RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                values,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        );
        boolean updateSuccess = update <= 0;
        if (updateSuccess) {
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
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;
    }

    public static boolean updateValuesForKeyWithBlank(String key) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTable.KeyValue.VALUE, "");

        boolean success = RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                contentValues,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;

        return success;
    }

    //http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    public static String getValue(String key) {
        String value = null;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.KeyValue.TABLE_NAME,
                    new String[]{DatabaseTable.KeyValue.VALUE},
                    DatabaseTable.KeyValue.KEY + "=?",
                    new String[]{key},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                value = cursor.getString(cursor.getColumnIndex(DatabaseTable.KeyValue.VALUE));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting value " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return value;
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
        public static final String XR_MAIL = API.key.XR_MAIL;
        public static final String XR_AUTH = API.key.XR_AUTH;
        public static final String WIFI_SYNC = "WIFI_SYNC";
        public static final String UNPROCESSED_DOCUMENT = "UNPROCESSED_DOCUMENT";
        public static final String SOCIAL_LOGIN = "SOCIAL_LOGIN";
        public static final String XR_DID = API.key.XR_DID;
        public static final String LAST_FETCHED = "LAST_FETCHED";
        //TODO(hht) Should we manage interval time for app from server

        private KEYS() {
        }
    }
}
