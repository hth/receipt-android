package com.receiptofi.android;

import com.receiptofi.android.db.DBHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsPage extends ParentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log_out);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addToBackStack(this);
	}
	public void logout(View view){
		finish();
		clearBackStack();
		DBHelper.clearKeyValues();
		startActivity(new Intent(this,LoginActivity.class));
	}
}
