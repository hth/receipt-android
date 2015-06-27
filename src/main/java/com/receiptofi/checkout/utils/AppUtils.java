package com.receiptofi.checkout.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private static final String APP_CONFIG = "config";
    public static final String CONF_FRIST_START = "isFristStart";
    private static final int RANDOM_STRING_SIZE = 4;

    static File image;
    static File receiptofiImgDir;
    private static Context homePageContext;

    public static String getImageFileFromURI(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex(filePathColumn[0]));
    }

    /**
     * Create place holder for new image that is about to be clicked.
     */
    public static File createImageFile() {
        try {
            image = File.createTempFile(
                    "Receipt_" + RandomString.newInstance(RANDOM_STRING_SIZE).nextString(), ".jpg",
                    getImageDir());

            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = "file:" + image.getAbsolutePath().replace("--", "-");

            return image;
        } catch (Exception e) {
            Log.e(TAG, "Failed to create image file");
            // TODO: handle exception
            if (image != null && image.exists()) {
                image.delete();
                image = null;
            }
            return null;
        }
    }

    public static String getImageFilePath() {
        if (image != null) {
            return image.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static Context getHomePageContext() {
        return homePageContext;
    }

    public static void setHomePageContext(Context context) {
        homePageContext = context;
    }

    public static boolean isNetworkConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (null == networkInfo) {
            Log.d(TAG, "Network status not connected");
        } else {
            Log.d(TAG, "Network status connected=" + networkInfo.isConnectedOrConnecting());
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (null == networkInfo) {
            Log.d(TAG, "Network status not connected");
        } else {
            Log.d(TAG, ConnectivityManager.TYPE_WIFI + " connected=" + (networkInfo.getType() == ConnectivityManager.TYPE_WIFI));
        }
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static void createImageDir() {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        receiptofiImgDir = new File(storageDir.getAbsolutePath() + File.separator + "Receiptofi");
        if (!receiptofiImgDir.exists()) {
            receiptofiImgDir.mkdir();
        }
    }

    public static File getImageDir() {
        return receiptofiImgDir;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
