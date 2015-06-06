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
import java.util.Date;
import java.util.UUID;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private final static String APP_CONFIG = "config";
    public final static String CONF_FRIST_START = "isFristStart";

    static File image;
    static File receiptofiImgDir;
    private static Context homePageContext;

    public static String getImageFileFromURI(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        c.moveToFirst();
        return c.getString(c.getColumnIndex(filePathColumn[0]));
    }

    public static File createImageFile() {
        // Create an image file name
        try {
            String imageFileName = "receipt_" + getRandomString();
            image = File.createTempFile(imageFileName, ".jpg", getImageDir());

            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = "file:" + image.getAbsolutePath();

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

    /**
     * Gets random string from UUID of first four characters or ISO date.
     */
    private static String getRandomString() {
        String randomString;
        String[] split = UUID.randomUUID().toString().split("-");
        if (split.length > 0) {
            if(split[0].length() > 4) {
                randomString = split[0].substring(0, 4);
            } else {
                randomString = split[0];
            }
        } else {
            randomString = ISO8601DateParser.toString(new Date());
        }
        return randomString;
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

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return null != networkInfo;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Log.d(TAG, "Wi-Fi is connected: " + wifiNetwork.isConnected());
        return wifiNetwork.isConnected();
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
