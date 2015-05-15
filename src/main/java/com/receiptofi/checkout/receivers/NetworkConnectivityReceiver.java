package com.receiptofi.checkout.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.UserUtils;

import java.util.ArrayList;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();
        if (queue != null && !queue.isEmpty()) {
            if (context != null && UserUtils.UserSettings.isStartImageUploadProcess(context)) {
                ImageUploaderService.start(context);
            }
        }
    }
}
