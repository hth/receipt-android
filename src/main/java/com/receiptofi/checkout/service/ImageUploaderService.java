package com.receiptofi.checkout.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.MainPageActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ImageResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils.UserSettings;

import java.util.ArrayList;

public class ImageUploaderService {
    private static final String TAG = ImageUploaderService.class.getSimpleName();

    private static ArrayList<Thread> allThreads = new ArrayList<>();
    private static ArrayList<Thread> imageUploadThreads = new ArrayList<>();
    private static boolean isServiceStarted = false;
    private static int MAX_NUMBER_THREAD = 5;
    private static int MAX_RETRY_UPLOAD = 5;
    private static Context context;

    private ImageUploaderService() {
    }

    public static void start(Context context) {
        // TODO Auto-generated method stub
        ImageUploaderService.context = context;
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();

        if (queue != null && !queue.isEmpty()) {
            for (int i = 0; i < queue.size() && imageUploadThreads.size() < MAX_NUMBER_THREAD; i++) {

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
                iModel.blobId = responseBundle.getString(DatabaseTable.ImageIndex.BLOB_ID);
                int unprocessedCount = responseBundle.getInt("unprocessedCount");

                iModel.updateStatus(true);
                updateProcessStatus(iModel);
                if (AppUtils.getHomePageContext() != null) {
                    Log.d(TAG, "Unprocessed document count " + unprocessedCount);
                    Message msg = new Message();
                    // KEVIN : Add to replace the HomeActivy by HomeFragment
//                    msg.what = HomeActivity.IMAGE_UPLOAD_SUCCESS;
                    msg.what = HomeFragment.IMAGE_UPLOAD_SUCCESS;
                    msg.obj = response;
                    msg.arg1 = unprocessedCount;
                    if (ReceiptofiApplication.isHomeActivityVisible()) {
                        // KEVIN : Add to replace the HomeActivy by HomeFragment
//                        ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                        ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendMessage(msg);
                    }
                }

                Log.d(TAG, "image upload done for " +iModel.imgPath);
            }

            @Override
            public void onException(ImageModel iModel, Exception exception) {
                iModel.updateStatus(false);
                updateProcessStatus(iModel);
                Log.e(TAG, "image upload failed due to exception " + iModel.imgPath + " reason " + exception.getLocalizedMessage(), exception);

                Message msg = new Message();
                // KEVIN : Add to replace the HomeActivy by HomeFragment
//                msg.what = HomeActivity.IMAGE_UPLOAD_FAILURE;
                msg.what = HomeFragment.IMAGE_UPLOAD_FAILURE;
                msg.obj = "Image upload failed " + exception.getLocalizedMessage() + ".";
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    // KEVIN : Add to replace the HomeActivy by HomeFragment
//                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                    ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendMessage(msg);
                }
            }

            @Override
            public void onError(ImageModel iModel, String Error) {
                // TODO Auto-generated method stub
                iModel.updateStatus(false);
                updateProcessStatus(iModel);
                Log.d("Image upload failed  ", iModel.imgPath);

                Message msg = new Message();
                // KEVIN : Add to replace the HomeActivy by HomeFragment
//                msg.what = HomeActivity.IMAGE_UPLOAD_FAILURE;
                msg.what = HomeFragment.IMAGE_UPLOAD_FAILURE;
                msg.obj = "Image upload failed. " + Error;
                if (ReceiptofiApplication.isHomeActivityVisible()) {
                    // KEVIN : Add to replace the HomeActivy by HomeFragment
//                    ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendMessage(msg);
                    ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendMessage(msg);
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
            // KEVIN : Add to replace the HomeActivy by HomeFragment
//            ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendEmptyMessage(HomeActivity.IMAGE_ADDED_TO_QUEUED);
            ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
        }
    }

}
