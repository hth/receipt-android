package com.receiptofi.android.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.receiptofi.android.services.ImageUploaderService;
import com.receiptofi.android.utils.AppUtils;
import com.receiptofi.android.utils.UserUtils;

public class NetworkConnectivityReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		if(context!=null){
			if(UserUtils.UserSettings.isStartImageUploadProcess(context)){
				ImageUploaderService.start(context);
			}
		}
		
	}

}
