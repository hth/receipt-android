package com.receiptofi.checkout;

import android.content.SharedPreferences;
import android.os.Bundle;
//import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.receiptofi.checkout.views.UserNamePreference;
import com.receiptofi.checkout.utils.UserUtils;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class PrefFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // set fields before inflating view from xml
            initializePref();
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            // set fields in the view
            setPrefs();

        }

        private void initializePref(){
            boolean wifiSync = UserUtils.UserSettings.isWifiSyncOnly();
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            SharedPreferences.Editor editor = pref.edit();
            // wifi setting from database
            editor.putBoolean(getString(R.string.key_pref_sync), wifiSync);
            editor.commit();
        }

        private void setPrefs(){
            // userlogin
            String username = UserUtils.getEmail();
            UserNamePreference usernamePref = (UserNamePreference)findPreference(getString(R.string.key_pref_username));
            usernamePref.setSummary(username);
        }

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
                Log.d(TAG, "wifi setting changed");
                boolean wifiSync = sharedPreferences.getBoolean(key, false);
                UserUtils.UserSettings.setWifiSync(getActivity().getApplicationContext(), wifiSync);
            } else if(key.equals(getString(R.string.key_pref_username))) {
                Log.d(TAG, "Username changed");
                String username = sharedPreferences.getString(key, null);
                updateUserCredential(key, username);
            } else if(key.equals(getString(R.string.key_pref_password))) {
                Log.d(TAG, "password changed");

            } else if(key.equals(getString(R.string.key_pref_notification))) {
                Log.d(TAG, "notification setting changed");
            } else {
                Log.d(TAG, "No match for key: " + key);
            }


        }

        private void updateUserCredential(String key, String data) {
            Log.d(TAG, "Update username");
            if(!UserUtils.isValidEmail(data)){
                //showErrorOnPrefScreen(key, getString(R.string.err_str_enter_valid_email));
               // SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
               // SharedPreferences.Editor editor = pref.edit();
                // wifi setting from database
               // editor.putString(key, UserUtils.getEmail());
               // editor.commit();
            }
        }

        private void showErrorOnPrefScreen(String key, String msg){
            //EditTextPreference pref = (EditTextPreference)findPreference(key);
           // pref.setSummary(msg);
        }
    }

}
