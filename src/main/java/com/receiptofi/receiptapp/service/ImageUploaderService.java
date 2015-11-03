package com.receiptofi.receiptapp.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ImageResponseHandler;
import com.receiptofi.receiptapp.http.ResponseParser;
import com.receiptofi.receiptapp.model.ImageModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.ConstantsJson;
import com.receiptofi.receiptapp.utils.UserUtils.UserSettings;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;

import junit.framework.Assert;

import java.util.ArrayList;

public class ImageUploaderService {
    private static final String TAG = ImageUploaderService.class.getSimpleName();

    private static ArrayList<Thread> allThreads = new ArrayList<>();
    private static ArrayList<Thread> imageUploadThreads = new ArrayList<>();
    private static boolean isServiceStarted = false;
    /** Changed number of thread to 1 because of multiple upload for same file. */
    private static int MAX_NUMBER_THREAD = 1;
    private static int MAX_RETRY_UPLOAD = 25;
    private static Context context;

    private ImageUploaderService() {
    }

    public static void start(Context context) {
        Assert.assertNotNull(context);
        ImageUploaderService.context = context;
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();
        Assert.assertNotNull(queue);

        Log.i(TAG, "Queue size=" + queue.size() + " imageUploadThreads.size()=" + imageUploadThreads.size());
        for (int i = 0; !queue.isEmpty() && i < queue.size() && imageUploadThreads.size() < MAX_NUMBER_THREAD; i++) {

            ImageModel iModel = queue.get(i);
            if (validateImageForUpload(iModel)) {
                isServiceStarted = true;
                Log.d(TAG, i + " image upload started for " + iModel.imgPath);
                Thread imageUploaderThread = getUploaderThread(context, iModel);
                imageUploadThreads.add(imageUploaderThread);
                allThreads.add(imageUploaderThread);
            }

            if (i == (queue.size() - 1)) {
                isServiceStarted = false;
            }
        }
    }

    private static boolean validateImageForUpload(ImageModel iModel) {
        if (iModel.imgPath != null
                && (iModel.imgStatus == null || iModel.imgStatus.equalsIgnoreCase(ImageModel.STATUS.UNPROCESSED))
                && !iModel.LOCK && iModel.noOfTimesTried <= MAX_RETRY_UPLOAD && iModel.noOfTimesTried > 0) {
            return true;
        } else {
            return false;
        }
    }

    private static Thread getUploaderThread(Context context, ImageModel iModel) {
        iModel.LOCK = true;

        Thread imageUploaderThread = ExternalCallWithOkHttp.uploadImage(context, API.UPLOAD_IMAGE_API, iModel, new ImageResponseHandler() {

            @Override
            public void onSuccess(ImageModel iModel, String response) {
                Bundle responseBundle = ResponseParser.getImageUploadResponse(response);
                iModel.blobId = responseBundle.getString(DatabaseTable.ImageIndex.BLOB_ID);
                String fileName = responseBundle.getString(ConstantsJson.UPLOADED_DOCUMENT_NAME);
                int unprocessedCount = responseBundle.getInt(ConstantsJson.UNPROCESSED_COUNT);
                KeyValueUtils.updateInsert(KeyValueUtils.KEYS.UNPROCESSED_DOCUMENT, String.valueOf(unprocessedCount));

                iModel.updateStatus(true);
                updateProcessStatus(iModel);
                if (AppUtils.getHomePageContext() != null) {
                    Log.d(TAG, "Unprocessed document count " + unprocessedCount);
                    Message msg = new Message();
                    msg.what = HomeFragment.IMAGE_UPLOAD_SUCCESS;
                    msg.obj = "Uploaded " + fileName + " successfully.";
                    msg.arg1 = unprocessedCount;
                    if (ReceiptofiApplication.isHomeActivityVisible()) {
                        ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).homeFragment.updateHandler.sendMessage(msg);
                    }
                }

                Log.d(TAG, "Image upload complete for " + iModel.imgPath);
            }

            @Override
            public void onException(ImageModel iModel, Exception exception) {
                iModel.updateStatus(false);
                updateProcessStatus(iModel);
                Log.e(TAG, "Image upload failed due to exception " + iModel.imgPath + " reason " + exception.getLocalizedMessage(), exception);

                Message msg = new Message();
                msg.what = HomeFragment.IMAGE_UPLOAD_FAILURE;
                msg.obj = exception.getLocalizedMessage();
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).homeFragment.updateHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(ImageModel iModel, String Error) {
                iModel.updateStatus(false);
                updateProcessStatus(iModel);
                Log.d("Image upload failed  ", iModel.imgPath);

                Message msg = new Message();
                msg.what = HomeFragment.IMAGE_UPLOAD_FAILURE;
                msg.obj = "Image upload failed. " + Error;
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).homeFragment.updateHandler.sendMessage(msg);
                }
            }
        });
        iModel.isTriedForUpload = true;
        iModel.uploaderThread = imageUploaderThread;

        return imageUploaderThread;
    }

    public static boolean isServiceConnected() {
        if (isServiceStarted) {
            return true;
        } else {
            return false;
        }
    }

    private synchronized static void updateProcessStatus(ImageModel model) {
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();
        if (model.imgStatus != null && model.imgStatus.equalsIgnoreCase(ImageModel.STATUS.PROCESSED)) {
            queue.remove(model);
        }
        imageUploadThreads.remove(model.uploaderThread);
        model.uploaderThread = null;
        Log.d(TAG, "Queuing done and now in updateProcessStatus");
        model.LOCK = false;
        for (Thread thread : allThreads) {
            if (!thread.isAlive()) {
                Log.w(TAG, "Thread  Died " + thread.getName());
            }
        }
        if (UserSettings.isStartImageUploadProcess(context)) {
            start(context);
        } else {
            ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).homeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);

        }
    }

}
