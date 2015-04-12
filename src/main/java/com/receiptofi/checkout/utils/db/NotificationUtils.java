package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.NotificationModel;

import java.util.LinkedList;
import java.util.List;

import static com.receiptofi.checkout.ReceiptofiApplication.RDH;

/**
 * User: hitender
 * Date: 3/25/15 7:37 PM
 */
public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();

    public static void insert(List<NotificationModel> notifications) {
        for (NotificationModel notification : notifications) {
            if (notification.isActive()) {
                insert(notification);
            } else {
                delete(notification.getId());
                Log.d(TAG, "Deleted notification: " + notification.getId());
            }
        }
    }

    private static void delete(String id) {
        RDH.getWritableDatabase().delete(
                DatabaseTable.Notification.TABLE_NAME,
                DatabaseTable.Notification.ID + " = '" + id + "'",
                null
        );
    }

    /**
     * Insert item in table.
     *
     * @param notification
     */
    private static void insert(NotificationModel notification) {
        ContentValues values = new ContentValues();
        values.put(DatabaseTable.Notification.ID, notification.getId());
        values.put(DatabaseTable.Notification.MESSAGE, notification.getMessage());
        values.put(DatabaseTable.Notification.VISIBLE, notification.isVisible());
        values.put(DatabaseTable.Notification.NOTIFICATION_TYPE, notification.getNotificationType());
        values.put(DatabaseTable.Notification.REFERENCE_ID, notification.getReferenceId());
        values.put(DatabaseTable.Notification.CREATED, notification.getCreated());
        values.put(DatabaseTable.Notification.UPDATED, notification.getUpdated());
        values.put(DatabaseTable.Notification.ACTIVE, notification.getUpdated());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Notification.TABLE_NAME,
                null,
                values
        );
    }

    public static List<NotificationModel> getAll() {
        Log.d(TAG, "Fetching all notifications");
        Cursor cursor = null;
        List<NotificationModel> list = new LinkedList<>();
        try {
            cursor = RDH.getReadableDatabase().query(
                    DatabaseTable.Notification.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseTable.Notification.CREATED + " desc"
            );

            if (null != cursor && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    NotificationModel notificationModel = new NotificationModel(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getInt(2) == 1,
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            cursor.getInt(7) == 1
                    );

                    list.add(notificationModel);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting notification message=" + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }

        return list;
    }
}
