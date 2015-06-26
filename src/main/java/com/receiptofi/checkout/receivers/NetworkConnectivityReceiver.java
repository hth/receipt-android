package com.receiptofi.checkout.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.MainPageActivity;
import com.receiptofi.checkout.adapters.ImageUpload;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.UserUtils;

import java.util.ArrayList;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkConnectivityReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        
        ArrayList<ImageModel> queue = ImageUpload.getImageQueue();
        if (queue.isEmpty()) {
            Log.d(TAG, "upload image queue is empty");
        } else {
            if (context != null && UserUtils.UserSettings.isStartImageUploadProcess(context)) {
                Log.d(TAG, "starting image upload for count=" + queue.size());
                ImageUploaderService.start(context);
            } else {
                // KEVIN : Add to replace the HomeActivy by HomeFragment
//                ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendEmptyMessage(HomeActivity.IMAGE_ADDED_TO_QUEUED);
                // TODO: Clean up below:
                if (Constants.KEY_NEW_PAGE) {
                    ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
                } else {
                    ((MainPageActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
                }
            }
        }
    }
}
