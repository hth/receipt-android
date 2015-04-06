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
            insert(notification);
        }
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

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Notification.TABLE_NAME,
                null,
                values
        );
    }

    public static List<NotificationModel> getAll() {
        Log.d(TAG, "Fetching all notifications");
        Cursor cursor = RDH.getReadableDatabase().query(
                DatabaseTable.Notification.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DatabaseTable.Notification.CREATED + " desc"
        );

        List<NotificationModel> list = new LinkedList<>();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                NotificationModel notificationModel = new NotificationModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getInt(2) == 1,
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6)
                );

                list.add(notificationModel);
            }
        }

        return list;
    }

}
