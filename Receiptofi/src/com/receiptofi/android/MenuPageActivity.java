package com.receiptofi.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuPageActivity extends ParentActivity {
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
		addTobackStack(this);
	}
	public void logout(View view){
		finish();
		clearbackStack();
		LoginActivity.isLoggedIn=false;
		startActivity(new Intent(this,LoginActivity.class));
	}
}
