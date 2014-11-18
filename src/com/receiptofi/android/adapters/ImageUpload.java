package com.receiptofi.android.adapters;

import java.util.ArrayList;

import com.receiptofi.android.ParentActivity;
import com.receiptofi.android.models.ImageModel;
import com.receiptofi.android.services.ImageUploaderService;
import com.receiptofi.android.utils.UserUtils.UserSettings;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

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
        boolean isAddedTodb = false;
        model.imgPath = imgFilePath;
        model.imgStatus = ImageModel.STATUS.UNPROCESSED;

        try {
            isAddedTodb = model.addToQueue();
            imageQueue.add(model);

            if (!ImageUploaderService.isServiceConnected() && isAddedTodb && UserSettings.isStartImageUploadProcess(context)) {
                Log.i("image added to queue", model.imgPath);
                ImageUploaderService.start(context);
            }
        } catch (SQLiteConstraintException e) {
            ((ParentActivity) context).showErrorMsg("This image already exists in upload queue.");
        }

    }

}
