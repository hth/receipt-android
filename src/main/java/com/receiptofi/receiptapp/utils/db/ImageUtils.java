package com.receiptofi.receiptapp.utils.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.model.ImageModel;

import java.util.ArrayList;

/**
 * User: hitender
 * Date: 11/4/15 2:29 AM
 */
public class ImageUtils {
    private static final String TAG = ImageModel.class.getSimpleName();

    public static boolean addToQueue(String imgDate, String imgPath) throws SQLiteConstraintException {
        long responseCode;

        ContentValues values = new ContentValues();
        values.put(DatabaseTable.UploadQueue.IMAGE_DATE, imgDate);
        values.put(DatabaseTable.UploadQueue.IMAGE_PATH, imgPath);
        values.put(DatabaseTable.UploadQueue.STATUS, ImageModel.STATUS.UNPROCESSED);

        responseCode = ReceiptofiApplication.RDH.getWritableDatabase().insertOrThrow(
                DatabaseTable.UploadQueue.TABLE_NAME,
                null,
                values);

        return responseCode != -1;
    }

    public static ArrayList<ImageModel> getAllUnprocessedImages() {
        ArrayList<ImageModel> models = new ArrayList<>();
        Cursor c = null;
        try {
            c = ReceiptofiApplication.RDH.getReadableDatabase().query
                    (DatabaseTable.UploadQueue.TABLE_NAME,
                            new String[]{DatabaseTable.UploadQueue.IMAGE_PATH},
                            null,
                            null,
                            null,
                            null,
                            null
                    );
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                ImageModel model = new ImageModel();
                model.imgPath = c.getString(0);
                models.add(model);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading images " + e.getLocalizedMessage(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return models;
    }

    public static void deleteFromDatabaseQueue(String imgPath) {
        ReceiptofiApplication.RDH.getWritableDatabase().delete(
                DatabaseTable.UploadQueue.TABLE_NAME,
                DatabaseTable.UploadQueue.IMAGE_PATH + "=?",
                new String[]{imgPath}
        );
    }
}
