package com.receiptofi.checkout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

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
            EditTextPreference usernamePref = (EditTextPreference)findPreference(getString(R.string.key_pref_username));
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
            }

        }
    }

}
