package com.receiptofi.receiptapp;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.db.DatabaseHandler;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.db.DBUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.utils.db.MonthlyReportUtils;

import net.danlew.android.joda.JodaTimeAndroid;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import io.fabric.sdk.android.Fabric;

@ReportsCrashes(
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
)
public class ReceiptofiApplication extends Application {
    private static final String TAG = ReceiptofiApplication.class.getSimpleName();

    public static DatabaseHandler RDH;
    private static boolean homeActivityVisible;

    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public static boolean isHomeActivityVisible() {
        return homeActivityVisible;
    }

    public static void homeActivityResumed() {
        homeActivityVisible = true;
    }

    public static void homeActivityPaused() {
        homeActivityVisible = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ReceiptofiApplication onCreate");
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
        Iconify.with(new FontAwesomeModule());

        RDH = DatabaseHandler.getsInstance(this);
        if (KeyValueUtils.doesTableExists() &&
                TextUtils.isEmpty(UserUtils.getAuth()) &&
                TextUtils.isEmpty(UserUtils.getEmail())) {
            Log.d(TAG, "Found authId empty, re-set data");
            DBUtils.initializeDefaults();
        } else {
            MonthlyReportUtils.fetchMonthly();
        }
        ImageUpload.initializeQueue();
        AppUtils.setHomePageContext(null);
        AppUtils.createImageDir();
    }
}
