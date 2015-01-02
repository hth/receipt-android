package com.receiptofi.checkout.services;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ImageResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.models.ImageModel;
import com.receiptofi.checkout.db.ReceiptDB;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils.UserSettings;

import java.util.ArrayList;

public class ImageUploaderService {

    private static ArrayList<Thread> allThreads = new ArrayList<>();
    private static ArrayList<Thread> imageUploadThreads = new ArrayList<>();
    private static boolean isServiceStarted = false;
    private static int MAX_NUMBER_THREAD = 5;
    private static int MAX_RETRY_UPLOAD = 5;
    private static Context context;

    public static void start(Context context) {
        // TODO Auto-generated method stub
        ImageUploaderService.context = context;
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();

        if (queue != null && queue.size() > 0) {
            for (int i = 0; i < queue.size() && imageUploadThreads.size() < MAX_NUMBER_THREAD; i++) {

                ImageModel iModel = queue.get(i);

                if (validateImageForUpload(iModel)) {
                    isServiceStarted = true;
                    Log.i(i + " image upload started for ", iModel.imgPath);

                    Thread imageUploaderThread = getUploaderThread(context, iModel);
                    imageUploadThreads.add(imageUploaderThread);
                    allThreads.add(imageUploaderThread);
                }

                if (i == (queue.size() - 1)) {
                    isServiceStarted = false;
                }
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

        Thread imageUploaderThread = ExternalCall.uploadImage(context, API.UPLOAD_IMAGE_API, iModel, new ImageResponseHandler() {

            @Override
            public void onSuccess(ImageModel iModel, String response) {
                // TODO Auto-generated method stub
                Bundle responseBundle = ResponseParser.getImageUploadResponse(response);
                iModel.blobId = responseBundle.getString(ReceiptDB.ImageIndex.BLOB_ID);
                int unprocessedCount = responseBundle.getInt("unprocessedCount");

                iModel.updateStatus(true);
                updateProcessStatus(iModel);
                if (AppUtils.getHomePageContext() != null) {
                    Log.i("UNPROCESSED DOCUMENT COUNT", "UNPROCESSED DOCUMENT COUNT" + unprocessedCount);

                    Message msg = new Message();
                    msg.what = HomeActivity.IMAGE_UPLOAD_SUCCESS;
                    msg.obj = response;
                    msg.arg1 = unprocessedCount;
                    if (ReceiptofiApplication.isHomeActivityVisible()) {
                        ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                    }
                }

                Log.i("image upload done for ", iModel.imgPath);
            }

            @Override
            public void onException(ImageModel iModel, Exception exception) {
                // TODO Auto-generated method stub
                iModel.updateStatus(false);
                updateProcessStatus(iModel);

                Log.i("image upload failed due to exception ", exception.getMessage());
                Log.i("image upload failed due to exception ", iModel.imgPath);

                Message msg = new Message();
                msg.what = HomeActivity.IMAGE_UPLOAD_FAILURE;
                msg.obj = "image upload failed due to exception: " + exception.getMessage();
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(ImageModel iModel, String Error) {
                // TODO Auto-generated method stub
                iModel.updateStatus(false);
                updateProcessStatus(iModel);

                Log.i("image upload failed  ", iModel.imgPath);
                Message msg = new Message();
                msg.what = HomeActivity.IMAGE_UPLOAD_FAILURE;
                msg.obj = "image upload failed due to error: " + Error;
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
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

        Log.i("queuing done", "In updateProcessStatus");
        model.LOCK = false;
        for (Thread thread : allThreads) {
            if (!thread.isAlive()) {

                Log.i("Thread  Died  ", thread.getName());
            }
        }
        if (UserSettings.isStartImageUploadProcess(context)) {
            start(context);
        }
    }

}
