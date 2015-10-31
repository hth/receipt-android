package com.receiptofi.receiptapp.db;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * User: hitender
 * Date: 10/30/15 9:05 PM
 */
public class Patch {
    private static final String TAG = Patch.class.getSimpleName();

    public Patch(int migrateFrom, int migrateTo, String buildNumber) {
        Log.d(TAG, "DB Migrate from " + migrateFrom + " to " + migrateTo + ". Since build " + buildNumber);
    }

    public void apply(SQLiteDatabase db) {
    }
}
