package com.receiptofi.receipts.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.receipts.db.DatabaseTable;
import com.receiptofi.receipts.http.API;

import static com.receiptofi.receipts.ReceiptofiApplication.RDH;

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

        return RDH.getWritableDatabase().update(
                DatabaseTable.KeyValue.TABLE_NAME,
                contentValues,
                DatabaseTable.KeyValue.KEY + "=?",
                new String[]{key}
        ) > 0;
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

    public static void deleteAll() {
        DBUtils.clearDB(DatabaseTable.KeyValue.TABLE_NAME);
    }

    public static class KEYS {
        public static final String XR_MAIL = API.key.XR_MAIL;
        public static final String XR_AUTH = API.key.XR_AUTH;
        public static final String WIFI_SYNC = "WIFI_SYNC";
        public static final String UNPROCESSED_DOCUMENT = "UNPROCESSED_DOCUMENT";
        public static final String SOCIAL_LOGIN = "SOCIAL_LOGIN";
        public static final String XR_DID = API.key.XR_DID;
        public static final String LAST_FETCHED = "LAST_FETCHED";
        public static final String GET_ALL_COMPLETE = "GET_ALL_COMPLETE";
        public static final String LATEST_APK = "LATEST_APK";
        //TODO(hht) Should we manage interval time for app from server

        private KEYS() {
        }
    }

    public static boolean doesTableExists() {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().rawQuery(
                    "SELECT count(*) FROM sqlite_master WHERE " +
                            "type = 'table' AND " +
                            "name = '" + DatabaseTable.KeyValue.TABLE_NAME + "'",
                    null);

            if (cursor.moveToNext()) {
                count = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error counting tables " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        Log.d(TAG, DatabaseTable.KeyValue.TABLE_NAME + " exists=" + (count > 0));
        return count > 0;
    }
}
