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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private static final String APP_CONFIG = "config";
    public static final String CONF_FRIST_START = "isFristStart";
    private static final int RANDOM_STRING_SIZE = 6;

    private static File image;
    private static File receiptofiImgDir;
    private static Context homePageContext;

    private AppUtils() {
    }

    public static String getImageFileFromURI(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        String imageAbsolutePath = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            imageAbsolutePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
        } catch (Exception e) {
            Log.e(TAG, "Error getting image absolute path " + e.getLocalizedMessage(), e);
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return imageAbsolutePath;
    }

    /**
     * Create place holder for new image that is about to be clicked.
     */
    public static File createImageFile() {
        try {
            image = new File(getImageDir() + File.separator + randomJPGFilename());
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

    private static String randomJPGFilename() {
        return "Receipt_" + RandomString.newInstance(RANDOM_STRING_SIZE).nextString() + ".jpg";
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
        boolean isWifi = false;
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (null == networkInfo) {
            Log.d(TAG, "Network status not connected");
        } else {
            isWifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            Log.d(TAG, ConnectivityManager.TYPE_WIFI + " connected=" + isWifi);
        }
        return networkInfo != null && isWifi;
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

    /**
     * Gets time for local time zone.
     *
     * @param date
     * @return
     */
    public static DateTime getDateTime(String date) {
        return Constants.ISO_J_DF.parseDateTime(date).withZone(DateTimeZone.getDefault());
    }

    public static String getDateTime(DateTime date) {
        return date.withZone(DateTimeZone.getDefault()).toString();
    }

    public static int sign(int i) {
        if (i == 0) return 0;
        if (i >> 31 != 0) return -1;
        return +1;
    }

    public static int sign(double f) {
        if (f != f) throw new IllegalArgumentException("NaN");
        if (f == 0) return 0;
        f *= Double.POSITIVE_INFINITY;
        if (f == Double.POSITIVE_INFINITY) return +1;
        if (f == Double.NEGATIVE_INFINITY) return -1;

        //this should never be reached, but I've been wrong before...
        throw new IllegalArgumentException("Unfathomed double");
    }

    public static boolean isPositive(int i) {
        return sign(i) >= 0;
    }
}
