package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Message;
import android.util.Log;

import com.receiptofi.receiptapp.HomeActivity;
import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ReceiptofiApplication;
import com.receiptofi.receiptapp.fragments.HomeFragment;
import com.receiptofi.receiptapp.fragments.HomeFragment1;
import com.receiptofi.receiptapp.model.ImageModel;
import com.receiptofi.receiptapp.service.ImageUploaderService;
import com.receiptofi.receiptapp.utils.UserUtils.UserSettings;
import com.receiptofi.receiptapp.utils.db.ImageUtils;

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
        imageQueue.addAll(ImageUtils.getAllUnprocessedImages());
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
            isAddedToDB = ImageUtils.addToQueue(model.imgDate, model.imgPath);
            imageQueue.add(model);

            if (!ImageUploaderService.isServiceConnected()
                    && isAddedToDB
                    && UserSettings.isStartImageUploadProcess(context)) {
                Log.i("image added to queue", model.imgPath);
                ImageUploaderService.start(context);
            } else {
                Message msg = new Message();
                msg.what = HomeFragment1.IMAGE_ADDED_TO_QUEUED;
                msg.obj = context.getApplicationContext().getString(R.string.image_added_to_queue);
                ((HomeActivity) context).homeFragment.updateHandler.sendMessage(msg);
            }
        } catch (SQLiteConstraintException e) {
            Message msg = new Message();
            msg.what = HomeFragment.IMAGE_ALREADY_QUEUED;
            msg.obj = context.getApplicationContext().getString(R.string.image_exists_in_queue);
            if (ReceiptofiApplication.isHomeActivityVisible()) {
                ((MainMaterialDrawerActivity) context).homeFragment.updateHandler.sendMessage(msg);
            }
        }
    }
}
