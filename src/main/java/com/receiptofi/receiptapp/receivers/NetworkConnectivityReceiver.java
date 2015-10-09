package com.receiptofi.receiptapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.model.ImageModel;
import com.receiptofi.receiptapp.service.ImageUploaderService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;

import java.util.ArrayList;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkConnectivityReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppUtils.isNetworkConnectedOrConnecting(context)) {
            Log.d(TAG, "Has net connection");
            whenConnected(context);
        } else {
            Log.d(TAG, "No net connection");
        }
    }

    private void whenConnected(Context context) {
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();
        if (queue.isEmpty()) {
            Log.d(TAG, "Image upload queue is empty");
        } else {
            if (null != context && UserUtils.UserSettings.isStartImageUploadProcess(context)) {
                Log.d(TAG, "Starting image upload for count=" + queue.size());
                ImageUploaderService.start(context);
            } else {
                MainMaterialDrawerActivity mainMaterialDrawerActivity = (MainMaterialDrawerActivity) AppUtils.getHomePageContext();
                if (null != mainMaterialDrawerActivity) {
                    mainMaterialDrawerActivity.homeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
                } else {
                    Log.e(TAG, "MainMaterialDrawerActivity is null. Cannot send message.");
                }
            }
        }
    }
}
