package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Message;
import android.util.Log;

import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.ReceiptofiApplication;
import com.receiptofi.checkout.fragments.HomeFragment;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.service.ImageUploaderService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants;
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
            } else {
                // TODO: Clean up below:
                if (Constants.KEY_NEW_PAGE) {
                    ((MainMaterialDrawerActivity) AppUtils.getHomePageContext()).mHomeFragment.updateHandler.sendEmptyMessage(HomeFragment.IMAGE_ADDED_TO_QUEUED);
                }
//                ((HomeActivity) AppUtils.getHomePageContext()).updateHandler.sendEmptyMessage(HomeActivity.IMAGE_ADDED_TO_QUEUED);
            }
        } catch (SQLiteConstraintException e) {
            Message msg = new Message();
            msg.what = HomeFragment.IMAGE_ALREADY_QUEUED;
            // TODO: Clean up below:
//            msg.what = HomeActivity.IMAGE_ALREADY_QUEUED;
            //TODO(hth) move this to XML file
            msg.obj = "This image already exists in upload queue.";
            if (ReceiptofiApplication.isHomeActivityVisible()) {
                // TODO: Clean up below:
                if (Constants.KEY_NEW_PAGE) {
                    ((MainMaterialDrawerActivity) context).mHomeFragment.updateHandler.sendMessage(msg);
                }
                // TODO: Clean up below:
//                ((HomeActivity) context).updateHandler.sendMessage(msg);
            }
        }
    }
}
