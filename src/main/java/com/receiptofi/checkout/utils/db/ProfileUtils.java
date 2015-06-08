package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ProfileModel;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 6/6/15 2:54 PM
 */
public class ProfileUtils {
    private static final String TAG = ProfileUtils.class.getSimpleName();

    private ProfileUtils() {
    }

    public static void insert(ProfileModel profile) {
        deleteAll();

        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Profile.FIRST_NAME, profile.getFirstName());
        values.put(DatabaseTable.Profile.LAST_NAME, profile.getLastName());
        values.put(DatabaseTable.Profile.MAIL, profile.getMail());
        values.put(DatabaseTable.Profile.NAME, profile.getName());
        values.put(DatabaseTable.Profile.RID, profile.getRid());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Profile.TABLE_NAME,
                null,
                values
        );

    }

    protected static void deleteAll() {
        DBUtils.clearDB(DatabaseTable.Profile.TABLE_NAME);
    }

    public static ProfileModel getProfile() {
        ProfileModel profile = null;
        Cursor cursor = null;
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Profile.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (null != cursor && cursor.getCount() > 0) {
                cursor.moveToNext();

                profile = new ProfileModel(
                        cursor.getString(0),
                        cursor.getString(2),
                        cursor.getString(4)
                );

                profile.setLastName(cursor.getString(1));
                profile.setName(cursor.getString(3));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding profile " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return profile;
    }
}
