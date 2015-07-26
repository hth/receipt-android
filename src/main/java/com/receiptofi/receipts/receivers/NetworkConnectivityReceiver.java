package com.receiptofi.receipts.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.receiptofi.receipts.MainMaterialDrawerActivity;
import com.receiptofi.receipts.adapters.ImageUpload;
import com.receiptofi.receipts.fragments.HomeFragment;
import com.receiptofi.receipts.model.ImageModel;
import com.receiptofi.receipts.service.ImageUploaderService;
import com.receiptofi.receipts.utils.AppUtils;
import com.receiptofi.receipts.utils.UserUtils;

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
                ((MainMaterialDrawerActivity) AppUtils.getHomePageContext())
                        .homeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
            }
        }
    }
}
