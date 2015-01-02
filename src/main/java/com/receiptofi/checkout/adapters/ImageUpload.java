package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.HomeActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.models.ImageModel;
import com.receiptofi.checkout.services.ImageUploaderService;
import com.receiptofi.checkout.utils.UserUtils.UserSettings;

import java.util.ArrayList;

public class ImageUpload {

    public static ArrayList<ImageModel> imageQueue = null;

    public static void initializeQueue() {
        if (imageQueue == null) {
            imageQueue = new ArrayList<>();
        }

        imageQueue.clear();
        imageQueue.addAll(ImageModel.getAllUnprocessedImages());
    }

    public static ArrayList<ImageModel> getImageQueue() {
        return imageQueue;
    }

    public static void process(Context context, String imgFilePath) {
        ImageModel model = new ImageModel();
        boolean isAddedToDB;
        model.imgPath = imgFilePath;
        model.imgStatus = ImageModel.STATUS.UNPROCESSED;

        try {
            isAddedToDB = model.addToQueue();
            imageQueue.add(model);

            if (!ImageUploaderService.isServiceConnected() && isAddedToDB && UserSettings.isStartImageUploadProcess(context)) {
                Log.i("image added to queue", model.imgPath);
                ImageUploaderService.start(context);
            }
        } catch (SQLiteConstraintException e) {
            Message msg = new Message();
            msg.what = HomeActivity.IMAGE_ALREADY_QUEUED;
            msg.obj = "This image already exists in upload queue.";
            if (ReceiptofiApplication.isHomeActivityVisible()) {
                ((HomeActivity) context).updateHandler.sendMessage(msg);
            }
        }
    }
}
