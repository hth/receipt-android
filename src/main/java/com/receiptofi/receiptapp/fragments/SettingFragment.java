package com.receiptofi.receiptapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.BuildConfig;
import com.receiptofi.receiptapp.LaunchActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.ApkVersionModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.service.DeviceService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;
import com.receiptofi.receiptapp.utils.db.DBUtils;
import com.receiptofi.receiptapp.utils.db.KeyValueUtils;
import com.receiptofi.receiptapp.views.LoginIdPreference;
import com.receiptofi.receiptapp.views.PasswordPreference;
import com.squareup.okhttp.Headers;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingFragment.class.getSimpleName();
    private SuperActivityToast progressToast;
    private static final int LOGIN_ID_UPDATE_SUCCESS = 0x2565;
    private static final int PASSWORD_UPDATE_SUCCESS = 0x2567;
    private static final int CHECK_UPDATE_SUCCESS = 0x2568;
    private ContextThemeWrapper ctw;
    private ApkVersionModel currentVersion;
    private ApkVersionModel latestVersion;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case LOGIN_ID_UPDATE_SUCCESS:
                    updatePrefs();
                    stopProgressToken();
                    showToast("Login Id updated successfully.", SuperToast.Duration.SHORT, SuperToast.Background.BLUE);
                    break;
                case PASSWORD_UPDATE_SUCCESS:
                    stopProgressToken();
                    showToast("Password updated successfully.", SuperToast.Duration.SHORT, SuperToast.Background.BLUE);
                    break;
                case CHECK_UPDATE_SUCCESS:
                    Log.d(TAG, "Update checked successfully");
                    stopProgressToken();
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctw = new ContextThemeWrapper(getActivity(), R.style.alert_dialog);
        try {
            currentVersion = AppUtils.parseVersion(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);

            /** Ignores first time, since the request is made when user lands on this screen. */
            latestVersion = AppUtils.parseVersion(KeyValueUtils.getValue(KeyValueUtils.KEYS.LATEST_APK));
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get app version, reason=" + e.getLocalizedMessage(), e);
        }

        // set fields before inflating view from xml
        initializePref();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        // set fields in the view
        updatePrefs();

        checkForLatestApk();
    }

    private void initializePref() {
        boolean wifiSync = UserUtils.UserSettings.isWifiSyncOnly();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        // wifi setting from database
        editor.putBoolean(getString(R.string.key_pref_sync), wifiSync);
        editor.apply();
    }

    private void updatePrefs() {
        /** Wi-Fi. */
        SwitchPreference wifiPref = (SwitchPreference) findPreference(getString(R.string.key_pref_sync));
        wifiPref.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_wifi)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        /** Notification. */
        SwitchPreference notificationPref = (SwitchPreference) findPreference(getString(R.string.key_pref_notification));
        notificationPref.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_bell)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        if (!"debug".equals(BuildConfig.BUILD_TYPE)) {
            PreferenceCategory mCategory = (PreferenceCategory) findPreference(getString(R.string.key_pref_preferences_id));
            mCategory.removePreference(notificationPref);
        }

        /** Login Id. */
        String username = UserUtils.getEmail();
        LoginIdPreference usernamePref = (LoginIdPreference) findPreference(getString(R.string.key_pref_login_id));
        usernamePref.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_envelope)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        usernamePref.setSummary(username);

        /** Password. */
        PasswordPreference passwordPreference = (PasswordPreference) findPreference(getString(R.string.key_pref_password));
        passwordPreference.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_lock)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        /** Handle Data Sync and Reset. */
        loadDataSyncReset();

        // Handle update and about preferences
        loadOther();
    }

    private void loadDataSyncReset() {
        Preference dataForceUpdate = findPreference(getString(R.string.key_pref_data_sync_id));
        dataForceUpdate.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_refresh)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        dataForceUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Force sync data pressed");
                AlertDialog alertDialog = dataSync();
                if (Build.VERSION.SDK_INT < 23) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));
                }
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                } else {
                    textView.setTextAppearance(R.style.alert_dialog_text_appearance_medium);

                    View title = alertDialog.findViewById(getContext().getResources().getIdentifier("alertTitle", "id", "android"));
                    ((TextView) title).setTextAppearance(R.style.alert_dialog);
                }
                return true;
            }
        });

        Preference dataDelete = findPreference(getString(R.string.key_pref_data_delete_id));
        dataDelete.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_trash_o)
                .colorRes(R.color.red)
                .actionBarSize());
        dataDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Delete data pressed");
                AlertDialog alertDialog = dataDelete();
                if (Build.VERSION.SDK_INT < 23) {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));
                }

                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                } else {
                    textView.setTextAppearance(R.style.alert_dialog_text_appearance_medium);

                    View title = alertDialog.findViewById(getContext().getResources().getIdentifier("alertTitle", "id", "android"));
                    ((TextView) title).setTextAppearance(R.style.alert_dialog);
                }
                return true;
            }
        });
    }

    private AlertDialog dataSync() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_data_sync_title)
                .setMessage(R.string.pref_data_sync_message)
                .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /** do nothing. */
                    }
                })
                .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Confirmed force sync");

                        if (AppUtils.isNetworkConnectedOrConnecting(getActivity())) {
                            showToast("Started syncing data.", SuperToast.Duration.EXTRA_LONG, SuperToast.Background.BLUE);
                            DBUtils.dbReInitializeNonKeyValues();
                            DBUtils.initializeDefaults();
                            DeviceService.getAll(getActivity());
                        } else {
                            showToast("No network available. Please try again when network is available.", SuperToast.Duration.EXTRA_LONG, SuperToast.Background.RED);
                        }
                    }
                })
                .setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_refresh)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    private AlertDialog dataDelete() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_data_delete_title)
                .setMessage(R.string.pref_data_delete_message)
                .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /** do nothing. */
                    }
                })
                .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Confirm delete data");
                        showToast("Started deleting data.", SuperToast.Duration.EXTRA_LONG, SuperToast.Background.BLUE);
                        DBUtils.dbReInitialize();
                        startActivity(new Intent(getActivity(), LaunchActivity.class));
                    }
                })
                .setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_trash_o)
                        .colorRes(R.color.red)
                        .actionBarSize())
                .show();
    }

    private void loadOther() {
        Preference perUpdate = findPreference(getString(R.string.key_pref_update_id));
        perUpdate.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_exchange)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        perUpdate.setTitle(getString(R.string.pref_update_title, BuildConfig.VERSION_NAME));
        if (AppUtils.isLatest(currentVersion, latestVersion)) {
            int color;
            if (Build.VERSION.SDK_INT < 23) {
                color = getResources().getColor(R.color.father_bg);
            } else {
                color = getResources().getColor(R.color.father_bg, null);
            }

            Spannable wordToSpan = new SpannableString(getString(R.string.pref_update_summary, latestVersion.version()));
            wordToSpan.setSpan(
                    new ForegroundColorSpan(color),
                            wordToSpan.length() - latestVersion.version().length(),
                            wordToSpan.length(),
                            0);

            perUpdate.setSummary(wordToSpan);
            perUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Log.d(TAG, "update is pressed");
                    AlertDialog alertDialog = update(latestVersion);
                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                    if (Build.VERSION.SDK_INT < 23) {
                        textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                    } else {
                        textView.setTextAppearance(R.style.alert_dialog_text_appearance_medium);
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));

                        View title = alertDialog.findViewById(getContext().getResources().getIdentifier("alertTitle", "id", "android"));
                        ((TextView) title).setTextAppearance(R.style.alert_dialog);
                    }
                    return true;
                }
            });
        } else {
            perUpdate.setSummary("");
        }

        Preference perAbout = findPreference(getString(R.string.key_pref_about_id));
        perAbout.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_info_circle)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        perAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                Log.d(TAG, "about is pressed");
                AlertDialog alertDialog = about();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                if (Build.VERSION.SDK_INT < 23) {
                    textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                } else {
                    textView.setTextAppearance(R.style.alert_dialog_text_appearance_medium);
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.black, null));

                    View title = alertDialog.findViewById(getContext().getResources().getIdentifier("alertTitle", "id", "android"));
                    ((TextView) title).setTextAppearance(R.style.alert_dialog);
                }
                return true;
            }
        });
    }

    private AlertDialog update(final ApkVersionModel latestVersion) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.pref_update_dialog_title, latestVersion.version()))
                .setMessage(getString(R.string.pref_update_message))
                .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /** do nothing. */
                    }
                })
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Trigger update process");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + getActivity().getPackageName()));
                        startActivity(goToMarket);
                    }
                })
                .setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_exchange)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    private AlertDialog about() {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pref_about_title)
                .setMessage(R.string.pref_about_message)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Yes pressed by about");
                    }
                })
                .setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_info_circle)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        /** Setup the background color. */
        View v = getView();
        Assert.assertNotNull("View is null", v);
        v.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged - key is: " + key);
        if (key.equals(getString(R.string.key_pref_sync))) {
            Log.d(TAG, "wifi setting changed- new value: " + sharedPreferences.getBoolean(key, false));
            boolean wifiSync = sharedPreferences.getBoolean(key, false);
            UserUtils.UserSettings.setWifiSync(getActivity().getApplicationContext(), wifiSync);
        } else if (key.equals(getString(R.string.key_pref_login_id))) {
            Log.d(TAG, "Username changed- new value: " + sharedPreferences.getString(key, null));
            String loginId = sharedPreferences.getString(key, null);
            updateLoginId(loginId);
            startProgressToken("Updating Login Id.");
        } else if (key.equals(getString(R.string.key_pref_password))) {
            Log.d(TAG, "password changed- new value: " + sharedPreferences.getString(key, null));
            String password = sharedPreferences.getString(key, null);
            updatePassword(password);
            startProgressToken("Updating Password.");
        } else if (key.equals(getString(R.string.key_pref_notification))) {
            Log.d(TAG, "notification setting changed- new value: " + sharedPreferences.getBoolean(key, false));
        } else {
            Log.d(TAG, "No match for key: " + key);
        }
    }

    private void updateLoginId(String data) {
        Log.d(TAG, "executing updateLoginId");
        if (!UserUtils.isValidEmail(data)) {
            showToast(getString(R.string.err_str_enter_valid_email), SuperToast.Duration.SHORT, SuperToast.Background.RED);
            resetLoginId();
        } else {
            JSONObject postData = new JSONObject();

            try {
                postData.put(API.key.SIGNUP_EMAIL, data);
            } catch (JSONException e) {
                Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
            }

            ExternalCallWithOkHttp.doPost(getActivity(), postData, API.SETTINGS_UPDATE_LOGIN_ID_API, IncludeAuthentication.YES, new ResponseHandler() {

                @Override
                public void onSuccess(Headers headers, String body) {
                    Log.d(TAG, "executing updateLoginId: onSuccess");
                    Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                    Map<String, String> headerData = ExternalCallWithOkHttp.parseHeader(headers, keys);
                    saveAuthKey(headerData);
                    updateHandler.sendEmptyMessage(LOGIN_ID_UPDATE_SUCCESS);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.e(TAG, "executing updateLoginId: onError: " + error);
                    resetLoginId();
                    stopProgressToken();
                    showToast(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.EXTRA_LONG, SuperToast.Background.RED);
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(TAG, "executing updateLoginId: onException: " + exception.getMessage());
                    resetLoginId();
                    stopProgressToken();
                    showToast(exception.getMessage(), SuperToast.Duration.SHORT, SuperToast.Background.RED);
                }
            });
        }
    }

    private void updatePassword(String data) {
        Log.d(TAG, "executing updatePassword");
        if (TextUtils.isEmpty(data)) {
            showToast(getString(R.string.err_str_enter_valid_password, Validation.PASSWORD_MIN_LENGTH), SuperToast.Duration.SHORT, SuperToast.Background.RED);
        } else {
            JSONObject postData = new JSONObject();

            try {
                postData.put(API.key.SIGNUP_PASSWORD, data);
            } catch (JSONException e) {
                Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
            }

            ExternalCallWithOkHttp.doPost(getActivity(), postData, API.SETTINGS_UPDATE_PASSWORD_API, IncludeAuthentication.YES, new ResponseHandler() {

                @Override
                public void onSuccess(Headers headers, String body) {
                    Log.d(TAG, "executing updatePassword: onSuccess");
                    Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                    Map<String, String> headerData = ExternalCallWithOkHttp.parseHeader(headers, keys);
                    saveAuthKey(headerData);
                    updateHandler.sendEmptyMessage(PASSWORD_UPDATE_SUCCESS);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.e(TAG, "executing updatePassword: onError: " + error);
                    stopProgressToken();
                    showToast(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.EXTRA_LONG, SuperToast.Background.RED);
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(TAG, "executing updatePassword: onException: " + exception.getMessage());
                    stopProgressToken();
                    showToast(exception.getMessage(), SuperToast.Duration.SHORT, SuperToast.Background.RED);
                }
            });
        }
    }

    private void checkForLatestApk() {
        ExternalCallWithOkHttp.doGet(getActivity(), API.LATEST_APK_API, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "executing updatePassword: onSuccess");
                KeyValueUtils.updateInsert(KeyValueUtils.KEYS.LATEST_APK, JsonParseUtils.parseLatestAPK(body));
                updateHandler.sendEmptyMessage(CHECK_UPDATE_SUCCESS);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.e(TAG, "executing updatePassword: onError: " + error);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(TAG, "executing updatePassword: onException: " + exception.getMessage());
            }
        });
    }

    private void resetLoginId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        // us old email
        editor.putString(getString(R.string.pref_login_title), UserUtils.getEmail());
        editor.apply();
    }

    protected void saveAuthKey(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            boolean success = KeyValueUtils.updateInsert(entry.getKey(), entry.getValue());
            if (!success) {
                Log.e(TAG, "Error while saving Auth data: key is:  " + entry.getKey() + "  value is:  " + entry.getValue());
            }
        }
    }

    public void showToast(final String message, final int duration, final int backgroundColor) {
        Assert.assertNotNull("Context should not be null", getActivity());
        if (TextUtils.isEmpty(message)) {
            return;
        }
        /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                superActivityToast.setText(message);
                superActivityToast.setDuration(duration);
                superActivityToast.setBackground(backgroundColor);
                superActivityToast.setTextColor(Color.WHITE);
                superActivityToast.setTouchToDismiss(true);
                superActivityToast.show();
            }
        });
    }

    private void startProgressToken(String message) {
        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText(message);
        progressToast.setIndeterminate(true);
        progressToast.setProgressIndeterminate(true);
        progressToast.show();
    }

    public void stopProgressToken() {
        if (null != progressToast && progressToast.isShowing()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressToast.dismiss();
                }
            });
        }
    }
}
