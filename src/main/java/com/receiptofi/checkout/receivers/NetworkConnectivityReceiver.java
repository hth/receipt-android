package com.receiptofi.checkout.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.UserUtils;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if (context != null) {
            if (UserUtils.UserSettings.isStartImageUploadProcess(context)) {
                ImageUploaderService.start(context);
            }
        }
    }
}
