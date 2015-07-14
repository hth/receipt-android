package com.receiptofi.checkout.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
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
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ApkVersionModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.Validation;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.views.LoginIdPreference;
import com.receiptofi.checkout.views.PasswordPreference;
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
    private String packageName = "";

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case LOGIN_ID_UPDATE_SUCCESS:
                    updatePrefs();
                    stopProgressToken();
                    showToast("Login Id updated successfully.", SuperToast.Duration.SHORT);
                    break;
                case PASSWORD_UPDATE_SUCCESS:
                    stopProgressToken();
                    showToast("Password updated successfully.", SuperToast.Duration.SHORT);
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
            packageName = getActivity().getPackageName();
            currentVersion = AppUtils.parseVersion(getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName);

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
        wifiPref.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_wifi)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        /** Notification. */
        SwitchPreference notificationPref = (SwitchPreference) findPreference(getString(R.string.key_pref_notification));
        notificationPref.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_bell)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        /** Login Id. */
        String username = UserUtils.getEmail();
        LoginIdPreference usernamePref = (LoginIdPreference) findPreference(getString(R.string.key_pref_login_id));
        usernamePref.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_envelope)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        usernamePref.setSummary(username);

        /** Password. */
        PasswordPreference passwordPreference = (PasswordPreference) findPreference(getString(R.string.key_pref_password));
        passwordPreference.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_lock)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());

        /** Handle Data Sync and Reset. */
        loadDataSyncReset();

        // Handle update and about preferences
        loadOther();
    }

    private void loadDataSyncReset() {
        Preference dataForceUpdate = findPreference(getString(R.string.key_pref_data_sync_id));
        dataForceUpdate.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_refresh)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        dataForceUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Force sync data pressed");
                AlertDialog alertDialog = dataSyncAlertDialog();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                return true;
            }
        });

        Preference dataDelete = findPreference(getString(R.string.key_pref_data_delete_id));
        dataDelete.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_trash_o)
                .colorRes(R.color.red)
                .actionBarSize());
        dataDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Delete data pressed");
                AlertDialog alertDialog = dataDeleteAlertDialog();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                return true;
            }
        });
    }

    private AlertDialog dataSyncAlertDialog() {
        return new AlertDialog.Builder(ctw)
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
                        DeviceService.getAll(getActivity());
                    }
                })
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_refresh)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    private AlertDialog dataDeleteAlertDialog() {
        return new AlertDialog.Builder(ctw)
                .setTitle(R.string.pref_data_delete_title)
                .setMessage(R.string.pref_data_delete_message)
                .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /** do nothing. */
                    }
                })
                .setPositiveButton(getString(R.string.dialog_button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Confirmed force sync");
                        DeviceService.getAll(getActivity());
                    }
                })
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_trash_o)
                        .colorRes(R.color.red)
                        .actionBarSize())
                .show();
    }

    private void loadOther() {
        Preference perUpdate = findPreference(getString(R.string.key_pref_update_id));
        perUpdate.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_exchange)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        perUpdate.setTitle(getString(R.string.pref_update_title, currentVersion.version()));
        if (AppUtils.isLatest(currentVersion, latestVersion)) {
            Spannable wordToSpan = new SpannableString(getString(R.string.pref_update_summary, latestVersion.version()));
            wordToSpan.setSpan(new ForegroundColorSpan(R.color.father_bg), wordToSpan.length() - latestVersion.version().length(), wordToSpan.length(), 0);
            perUpdate.setSummary(wordToSpan);
            perUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Log.d(TAG, "update is pressed");
                    AlertDialog alertDialog = updateAlertDialog(latestVersion);
                    TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                    textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                    return true;
                }
            });
        } else {
            perUpdate.setSummary("");
        }

        Preference perAbout = findPreference(getString(R.string.key_pref_about_id));
        perAbout.setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_info_circle)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize());
        perAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                Log.d(TAG, "about is pressed");
                AlertDialog alertDialog = aboutAlertDialog();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setTextAppearance(getActivity(), R.style.alert_dialog_text_appearance_medium);
                return true;
            }
        });
    }

    private AlertDialog updateAlertDialog(final ApkVersionModel latestVersion) {
        return new AlertDialog.Builder(ctw)
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
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + packageName));
                        startActivity(goToMarket);
                    }
                })
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_exchange)
                        .colorRes(R.color.app_theme_bg)
                        .actionBarSize())
                .show();
    }

    private AlertDialog aboutAlertDialog() {
        return new AlertDialog.Builder(ctw)
                .setTitle(R.string.pref_about_title)
                .setMessage(R.string.pref_about_message)
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Yes pressed by about");
                    }
                })
                .setIcon(new IconDrawable(getActivity(), Iconify.IconValue.fa_info_circle)
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
            showToast(getString(R.string.err_str_enter_valid_email), SuperToast.Duration.SHORT);
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
                    showToast(JsonParseUtils.parseError(error), SuperToast.Duration.EXTRA_LONG);
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(TAG, "executing updateLoginId: onException: " + exception.getMessage());
                    resetLoginId();
                    stopProgressToken();
                    showToast(exception.getMessage(), SuperToast.Duration.SHORT);
                }
            });
        }
    }

    private void updatePassword(String data) {
        Log.d(TAG, "executing updatePassword");
        if (TextUtils.isEmpty(data)) {
            showToast(getString(R.string.err_str_enter_valid_password, Validation.PASSWORD_MIN_LENGTH), SuperToast.Duration.SHORT);
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
                    showToast(JsonParseUtils.parseError(error), SuperToast.Duration.EXTRA_LONG);
                }

                @Override
                public void onException(Exception exception) {
                    Log.e(TAG, "executing updatePassword: onException: " + exception.getMessage());
                    stopProgressToken();
                    showToast(exception.getMessage(), SuperToast.Duration.SHORT);
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

    public void showToast(final String message, final int duration) {
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
                superActivityToast.setBackground(SuperToast.Background.BLUE);
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
