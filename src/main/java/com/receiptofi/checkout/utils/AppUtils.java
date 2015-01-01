package com.receiptofi.checkout.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {

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
            String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HHmmss").format(new Date());
            String imageFileName = "Receipt_android_" + timeStamp + "_";

            image = File.createTempFile(imageFileName, ".jpg", getImageDir());

            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = "file:" + image.getAbsolutePath();

            return image;
        } catch (Exception e) {
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

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMobileInternetConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInternet = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mobileInternet.isConnected()) {
            return true;
        } else {
            return false;
        }
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
}
