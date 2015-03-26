package com.receiptofi.checkout.utils.db;

import android.content.ContentValues;

import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.NotificationModel;

import java.util.List;

/**
 * User: hitender
 * Date: 3/25/15 7:37 PM
 */
public class NotificationUtils {
    private static final String TAG = NotificationUtils.class.getSimpleName();

    public static void insert(List<NotificationModel> notifications) {
        for(NotificationModel notification : notifications) {
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
        values.put(DatabaseTable.Notification.NOTIFICATION_TYPE, notification.getNotificationType());
        values.put(DatabaseTable.Notification.VISIBLE, notification.isVisible());
        values.put(DatabaseTable.Notification.REFERENCE_ID, notification.getReferenceId());

        ReceiptofiApplication.RDH.getWritableDatabase().insert(
                DatabaseTable.Notification.TABLE_NAME,
                null,
                values
        );
    }
}
