package com.receiptofi.checkout.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.views.LoginIdPreference;
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
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set fields before inflating view from xml
        initializePref();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        // set fields in the view
        updatePrefs();
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
        // login id
        String username = UserUtils.getEmail();
        LoginIdPreference usernamePref = (LoginIdPreference) findPreference(getString(R.string.key_pref_login_id));
        usernamePref.setSummary(username);
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        // Setup the background color
        View v = getView();
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
                    Log.d(TAG, "executing updateLoginId: onError: " + error);
                    resetLoginId();
                    stopProgressToken();
                    showToast(JsonParseUtils.parseError(error), SuperToast.Duration.EXTRA_LONG);
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing updateLoginId: onException: " + exception.getMessage());
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
            showToast(getString(R.string.err_str_enter_valid_password), SuperToast.Duration.SHORT);
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
                    updateHandler.sendEmptyMessage(LOGIN_ID_UPDATE_SUCCESS);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.d(TAG, "executing updatePassword: onError: " + error);
                    stopProgressToken();
                    showToast(JsonParseUtils.parseError(error), SuperToast.Duration.EXTRA_LONG);
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing updatePassword: onException: " + exception.getMessage());
                    stopProgressToken();
                    showToast(exception.getMessage(), SuperToast.Duration.SHORT);
                }
            });
        }
    }

    private void resetLoginId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        // us old email
        editor.putString(getString(R.string.pref_login_id), UserUtils.getEmail());
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
