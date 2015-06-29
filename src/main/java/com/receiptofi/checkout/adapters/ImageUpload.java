package com.receiptofi.checkout.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.UserUtils.UserSettings;

import java.util.ArrayList;

public class ImageUpload {

    public static ArrayList<ImageModel> imageQueue = null;

    private ImageUpload() {
    }

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

            if (!ImageUploaderService.isServiceConnected() &&
                    isAddedToDB &&
                    UserSettings.isStartImageUploadProcess(context)) {
                Log.i("image added to queue", model.imgPath);
                ImageUploaderService.start(context);
            } else {
                Message msg = new Message();
                msg.what = HomeFragment.IMAGE_ADDED_TO_QUEUED;
                msg.obj = context.getApplicationContext().getString(R.string.image_added_to_queue);
                ((MainMaterialDrawerActivity) context).mHomeFragment.updateHandler.sendMessage(msg);
            }
        } catch (SQLiteConstraintException e) {
            Message msg = new Message();
            msg.what = HomeFragment.IMAGE_ALREADY_QUEUED;
            msg.obj = context.getApplicationContext().getString(R.string.image_exists_in_queue);
            if (ReceiptofiApplication.isHomeActivityVisible()) {
                ((MainMaterialDrawerActivity) context).mHomeFragment.updateHandler.sendMessage(msg);
            }
        }
    }
}
