package com.receiptofi.checkout.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.receiptofi.checkout.ReceiptofiApplication;

import java.util.ArrayList;

public class ImageModel {

    public String blobId;
    public String imgPath;
    public String imgDate;
    public String imgStatus;
    public boolean LOCK = false;
    public boolean isTriedForUpload = false;
    public int noOfTimesTried = 1;
    public Thread uploaderThread;

    public static ArrayList<ImageModel> getAllUnprocessedImages() {
        ArrayList<ImageModel> models = new ArrayList<ImageModel>();
        Cursor c = ReceiptofiApplication.RDH.getReadableDatabase().query
                (ReceiptDB.UploadQueue.TABLE_NAME,
                        new String[]{ReceiptDB.UploadQueue.IMAGE_PATH},
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
        return models;
    }

    public boolean addToQueue() throws SQLiteConstraintException {
        long responseCode = -1;

        ContentValues values = new ContentValues();
        values.put(ReceiptDB.UploadQueue.IMAGE_DATE, imgDate);
        values.put(ReceiptDB.UploadQueue.IMAGE_PATH, imgPath);
        values.put(ReceiptDB.UploadQueue.STATUS, STATUS.UNPROCESSED);

        responseCode = ReceiptofiApplication.RDH.getWritableDatabase().insertOrThrow(ReceiptDB.UploadQueue.TABLE_NAME, null, values);

        if (responseCode != -1) {
            return true;
        } else {
            return false;
        }

    }

    public void deleteFromQueue() {
        ReceiptofiApplication.RDH.getWritableDatabase().delete(ReceiptDB.UploadQueue.TABLE_NAME, ReceiptDB.UploadQueue.IMAGE_PATH + "=?", new String[]{imgPath});
    }

    public synchronized boolean updateStatus(boolean isProcessed) {
        if (isProcessed) {
            // if image is uploaded successfully then noOfTimesTried =0 , which is useful in next iteration of queue.
            noOfTimesTried = 0;
        } else {
            noOfTimesTried++;
        }

        ContentValues uploadValues = new ContentValues();
        uploadValues.put(ReceiptDB.UploadQueue.IMAGE_DATE, imgDate);
        uploadValues.put(ReceiptDB.UploadQueue.IMAGE_PATH, imgPath);

        if (isProcessed) {
            imgStatus = STATUS.PROCESSED;
            uploadValues.put(ReceiptDB.UploadQueue.STATUS, STATUS.PROCESSED);
        } else {
            imgStatus = STATUS.UNPROCESSED;
            uploadValues.put(ReceiptDB.UploadQueue.STATUS, STATUS.UNPROCESSED);
        }

        ReceiptofiApplication.RDH.getWritableDatabase().update(
                ReceiptDB.UploadQueue.TABLE_NAME,
                uploadValues,
                ReceiptDB.UploadQueue.IMAGE_PATH + "=?",
                new String[]{imgPath});

        ContentValues imageIndexValue = new ContentValues();

        imageIndexValue.put(ReceiptDB.ImageIndex.IMAGE_PATH, imgPath);
        imageIndexValue.put(ReceiptDB.ImageIndex.BLOB_ID, blobId);

        ReceiptofiApplication.RDH.getWritableDatabase().insert(ReceiptDB.ImageIndex.TABLE_NAME, null, imageIndexValue);

        if (isProcessed) {
            deleteFromQueue();
        }

        return true;
    }

    public static final class STATUS {
        public static final String PROCESSED = "P";
        public static final String UNPROCESSED = "U";
    }
}
