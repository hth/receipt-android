package com.receiptofi.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ParentActivity extends Activity {

    protected Handler uiThread = new Handler();
    protected Calendar calender = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public void startFragment(Fragment fragment, boolean isaddToBackStack, int viewId) {
        // TODO Auto-generated method stub
        getFragmentManager().beginTransaction().replace(viewId, fragment)
                .addToBackStack(null).commit();
    }

    private static ArrayList<Activity> backStack;
    private ProgressDialog loader;

    protected void addToBackStack(Activity activity) {
        if (backStack == null) {
            backStack = new ArrayList<Activity>();
        }

        backStack.add(activity);
    }

    protected ArrayList<Activity> getBackStack() {
        return backStack;
    }

    protected void clearBackStack() {
        if (backStack != null) {
            for (int i = 0; i < (backStack.size() - 1); i++) {
                Activity activity = backStack.get(i);
                if (backStack.size() > 1) {
                    activity.finish();
                }
            }
        }
    }

    public void showLoader(String msg) {
        loader = new ProgressDialog(this);
        loader.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.setIndeterminate(true);
        loader.setMessage(msg);
        loader.show();
    }

    public void hideLoader() {
        if (loader != null) {
            loader.dismiss();
        }
        loader = null;
    }

    public void showErrorMsg(final String msg) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ParentActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

        });
    }
}
