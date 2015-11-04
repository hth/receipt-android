package com.receiptofi.receiptapp.model;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Objects;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.service.ImageUploaderService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.db.ImageUtils;
import com.receiptofi.receiptapp.utils.db.NotificationUtils;

import org.joda.time.DateTime;

public class ImageModel {
    private static final String TAG = ImageModel.class.getSimpleName();

    public String blobId;

    @NonNull
    public String imgPath;

    public String imgDate;
    public String imgStatus;
    public boolean LOCK = false;
    public boolean isTriedForUpload = false;
    public int noOfTimesTried = 1;
    public Thread uploaderThread;

    public static final String DOCUMENT_UPLOAD_FAILED_NOTIFICATION_TYPE = "DOCUMENT_UPLOAD_FAILED";

    public synchronized boolean updateStatus(boolean isProcessed) {
        if (isProcessed) {
            // if image is uploaded successfully then noOfTimesTried =0 , which is useful in next iteration of queue.
            noOfTimesTried = 0;
        } else {
            noOfTimesTried++;
        }

        ContentValues uploadValues = new ContentValues();
        uploadValues.put(DatabaseTable.UploadQueue.IMAGE_DATE, imgDate);
        uploadValues.put(DatabaseTable.UploadQueue.IMAGE_PATH, imgPath);

        if (isProcessed) {
            imgStatus = STATUS.PROCESSED;
            uploadValues.put(DatabaseTable.UploadQueue.STATUS, STATUS.PROCESSED);
        } else {
            imgStatus = STATUS.UNPROCESSED;
            uploadValues.put(DatabaseTable.UploadQueue.STATUS, STATUS.UNPROCESSED);
        }

        ReceiptofiApplication.RDH.getWritableDatabase().update(
                DatabaseTable.UploadQueue.TABLE_NAME,
                uploadValues,
                DatabaseTable.UploadQueue.IMAGE_PATH + "=?",
                new String[]{imgPath});

        ContentValues imageIndexValue = new ContentValues();

        imageIndexValue.put(DatabaseTable.ImageIndex.IMAGE_PATH, imgPath);
        imageIndexValue.put(DatabaseTable.ImageIndex.BLOB_ID, blobId);

        ReceiptofiApplication.RDH.getWritableDatabase().insert(DatabaseTable.ImageIndex.TABLE_NAME, null, imageIndexValue);

        if (isProcessed || noOfTimesTried > ImageUploaderService.MAX_RETRY_UPLOAD) {
            if (noOfTimesTried > ImageUploaderService.MAX_RETRY_UPLOAD) {
                Log.i(TAG, "Failed to upload image after " + ImageUploaderService.MAX_RETRY_UPLOAD + " tries. Deleting from queue.");

                NotificationModel notificationModel = new NotificationModel(
                        imgPath,
                        "Receipt '" + AppUtils.getFileName(imgPath) + "' reached maximum number of upload tries. " +
                                "Removed receipt from queue. " +
                                "Please re-try uploading receipt.",
                        true,
                        DOCUMENT_UPLOAD_FAILED_NOTIFICATION_TYPE,
                        "",
                        DateTime.now().toString(),
                        DateTime.now().toString(),
                        true);
                NotificationUtils.insert(notificationModel);
            }

            ImageUtils.deleteFromDatabaseQueue(imgPath);

            ImageModel imageModel = new ImageModel();
            imageModel.imgPath = imgPath;
            ImageUpload.imageQueue.remove(imageModel);
            Log.i(TAG, "Removed image from db queue and imageQueue");

            /** Delete old notifications is any. */
            NotificationUtils.deleteOldNotificationForUploadFailed();
        }

        return true;
    }

    public static final class STATUS {
        public static final String PROCESSED = "P";
        public static final String UNPROCESSED = "U";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageModel that = (ImageModel) o;
        return Objects.equal(imgPath, that.imgPath);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(imgPath);
    }
}
