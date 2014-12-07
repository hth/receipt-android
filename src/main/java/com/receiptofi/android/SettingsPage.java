package com.receiptofi.android;

import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.utils.UserUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

public class SettingsPage extends ParentActivity {

    ToggleButton wifiSyncBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        wifiSyncBtn = (ToggleButton) findViewById(R.id.wifiSyncBtn);
        initializeUI();
    }

    private void initializeUI() {
        // TODO Auto-generated method stub
        boolean wifiOnly = UserUtils.UserSettings.isWifiSyncOnly();
        if (wifiOnly) {
            wifiSyncBtn.setChecked(true);
        } else {
            wifiSyncBtn.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addToBackStack(this);
    }

    public void logout(View view) {
        finish();
        clearBackStack();
        KeyValue.clearKeyValues();
        KeyValue.clearReceiptsDB();
        startActivity(new Intent(this, LaunchActivity.class));
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void onWifiSyncToggle(View view) {
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            UserUtils.UserSettings.setWifiSync(this, true);
        } else {
            UserUtils.UserSettings.setWifiSync(this, false);
        }
    }
}
