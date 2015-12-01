package com.receiptofi.receiptapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.receiptofi.receiptapp.model.ApkVersionModel;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private static final String APP_CONFIG = "config";
    public static final String CONF_FRIST_START = "isFristStart";
    private static final int RANDOM_STRING_SIZE = 6;

    private static File image;
    private static File receiptofiImgDir;
    private static Context homePageContext;
    private static DecimalFormat formatter;

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

    public static String getFileName(String file) {
        if (file.contains(".")) {
            return file.substring(file.lastIndexOf("/") + 1);
        }
        return "";
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

    public static ApkVersionModel parseVersion(String version) {
        if (null == version || !version.contains(".")) {
            return null;
        }

        String[] split = version.split("\\.");
        ApkVersionModel apkVersionModel = null;
        if (split.length == 4) {
            apkVersionModel = new ApkVersionModel(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3]));
        } else if (split.length == 3) {
            apkVersionModel = new ApkVersionModel(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
        }
        return apkVersionModel;
    }

    /**
     * Compares is the existing version is less than the newer version number received from server.
     *
     * @param older
     * @param newer
     * @return
     */
    public static boolean isLatest(ApkVersionModel older, ApkVersionModel newer) {
        return newer != null &&
                (newer.getMajor() > older.getMajor() ||
                        newer.getMinor() > older.getMinor() ||
                        newer.getPatch() > older.getPatch());
    }

    //TODO add support formatting price based on receipt location
    public static DecimalFormat currencyFormatter() {
        if (formatter == null) {
            formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(getHomePageContext().getResources().getConfiguration().locale);
            String symbol = formatter.getCurrency().getSymbol();
            formatter.setNegativePrefix("-" + symbol); // or "-"+symbol if that's what you need
            formatter.setNegativeSuffix("");
        }
        return formatter;
    }

    public static boolean isSocialAccount() {
        return Boolean.valueOf(KeyValueUtils.getValue(KeyValueUtils.KEYS.SOCIAL_LOGIN));
    }
}
