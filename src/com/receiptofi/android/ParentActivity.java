package com.receiptofi.android;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class ParentActivity extends Activity {
	
	Handler uiThread = new Handler();
	protected Calendar calender = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	private static ArrayList<Activity> backstack;
	ProgressDialog loader;

	protected void addTobackStack(Activity activity) {
		if(backstack==null)
			backstack=new ArrayList<Activity>();
		
		backstack.add(activity);
	}
	protected ArrayList<Activity> getBackStack(){
		return backstack;
	}
	protected void clearbackStack() {
		if(backstack!=null){
			for(int i=0;i<(backstack.size()-1);i++){
				Activity activity=backstack.get(i);
				if(backstack.size()>1 ){
					activity.finish();
				}
			}
		}
	}
	protected void showloader(String msg) {
		loader=new ProgressDialog(this);
		loader.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.setMessage(msg);
		loader.show();

	}
	protected void hideloader() {
		if(loader!=null){
			loader.dismiss();
		}
		loader=null;
	}

	protected void showErrorMsg(final String msg) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ParentActivity.this, msg, Toast.LENGTH_SHORT)
						.show();
			}

		});
	}
	
}
