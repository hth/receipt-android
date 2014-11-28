package com.receiptofi.android.receivers;

import com.receiptofi.android.services.ImageUploaderService;
import com.receiptofi.android.utils.UserUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
