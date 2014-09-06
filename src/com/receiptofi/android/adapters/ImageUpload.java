package com.receiptofi.android.adapters;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteConstraintException;
import android.os.IBinder;
import android.util.Log;

import com.receiptofi.android.ParentActivity;
import com.receiptofi.android.ReceiptofiApplication;
import com.receiptofi.android.models.ImageModel;
import com.receiptofi.android.models.ReceiptDB;
import com.receiptofi.android.models.ReceiptModel;
import com.receiptofi.android.services.ImageUploaderService;
import com.receiptofi.android.utils.UserUtils;
import com.receiptofi.android.utils.UserUtils.UserSettings;

public class ImageUpload {

	public static ArrayList<ImageModel> imageQueue = null;

	public static final void initializeQueue() {
		
		if (imageQueue == null) {
			imageQueue = new ArrayList<ImageModel>();
		}
		
		imageQueue.clear();
		imageQueue.addAll(ImageModel.getAllUnprocessedImages());
	}
	
	public static ArrayList<ImageModel> getImageQueue() {
		return imageQueue;
	}

	public static final void process(Context context, String imgFilePath) {
		
		ImageModel model = new ImageModel();
		boolean isAddedTodb =false;
		model.imgPath = imgFilePath;
		model.imgStatus=ImageModel.STATUS.UNPROCESSED;
		
		try {
			isAddedTodb =model.addToQueue();
			imageQueue.add(model);
			
			if (!ImageUploaderService.isServiceConnected() && isAddedTodb && UserSettings.isStartImageUploadProcess(context)) {
				Log.i("image added to queue", model.imgPath);
				ImageUploaderService.start(context);
			}
		} catch (SQLiteConstraintException e) {
			((ParentActivity)context).showErrorMsg("Image is already in Queue");
		}
		
	}

}
