package com.receiptofi.checkout.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.db.KeyValueUtils;
import com.receiptofi.checkout.views.LoginIdPreference;
import com.receiptofi.checkout.views.ToastBox;
import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingFragment.class.getSimpleName();

    protected static final int LOGIN_ID_UPDATE_SUCCESS = 0x2565;
    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case LOGIN_ID_UPDATE_SUCCESS:
                    updatePrefs();
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
            updateLoginId(key, loginId);
        } else if (key.equals(getString(R.string.key_pref_password))) {
            Log.d(TAG, "password changed- new value: " + sharedPreferences.getString(key, null));
            String password = sharedPreferences.getString(key, null);
            updatePassword(key, password);
        } else if (key.equals(getString(R.string.key_pref_notification))) {
            Log.d(TAG, "notification setting changed- new value: " + sharedPreferences.getBoolean(key, false));
            //TODO add this
        } else {
            Log.d(TAG, "No match for key: " + key);
        }
    }

    private void updateLoginId(String key, String data) {
        Log.d(TAG, "executing updateLoginId");
        if (!UserUtils.isValidEmail(data)) {
            showErrorMsg(getString(R.string.err_str_enter_valid_email));
            resetLoginId();
        } else {
            JSONObject postData = new JSONObject();

            try {
                postData.put(API.key.SETTING_UPDATE_LOGIN_ID, data);
            } catch (JSONException e) {
                Log.d(TAG, "Exception while adding postdata: " + e.getMessage());
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
                    ToastBox.makeText(getActivity(), JsonParseUtils.parseError(error), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing updateLoginId: onException: " + exception.getMessage());
                    resetLoginId();
                    ToastBox.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updatePassword(String key, String data) {
        Log.d(TAG, "executing updatePassword");
        if (TextUtils.isEmpty(data)) {
            showErrorMsg(getString(R.string.err_str_enter_valid_password));
        } else {
            JSONObject postData = new JSONObject();

            try {
                postData.put(API.key.SETTING_UPDATE_PASSWORD, data);
            } catch (JSONException e) {
                Log.d(TAG, "Exception while adding postdata: " + e.getMessage());
            }

            ExternalCallWithOkHttp.doPost(getActivity(), postData, API.SETTINGS_UPDATE_PASSWORD_API, IncludeAuthentication.YES, new ResponseHandler() {

                @Override
                public void onSuccess(Headers headers, String body) {
                    Log.d(TAG, "executing updatePassword: onSuccess");
                    Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                    Map<String, String> headerData = ExternalCallWithOkHttp.parseHeader(headers, keys);
                    saveAuthKey(headerData);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.d(TAG, "executing updatePassword: onError: " + error);
                    ToastBox.makeText(getActivity(), JsonParseUtils.parseError(error), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing updatePassword: onException: " + exception.getMessage());
                    ToastBox.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void resetLoginId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        // us old email
        editor.putString(getString(R.string.pref_login_id), UserUtils.getEmail());
        editor.commit();
    }

    protected void saveAuthKey(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            boolean success = KeyValueUtils.updateInsert(entry.getKey(), entry.getValue());
            if (!success) {
                Log.e(TAG, "Error while saving Auth data: key is:  " + entry.getKey() + "  value is:  " + entry.getValue());
            }
        }
    }

    public void showErrorMsg(final String msg) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                ToastBox.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
