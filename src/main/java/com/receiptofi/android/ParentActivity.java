package com.receiptofi.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.utils.ReceiptUtils;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ParentActivity extends Activity {

    private static final String TAG = ParentActivity.class.getSimpleName();

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

    protected void authenticateUser(boolean isSocialLogin, Bundle data) {
        Log.d(TAG, "executing authenticateUser");
        if (isSocialLogin) {
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        JSONObject postData = new JSONObject();

        try {
            postData.put(API.key.PID, data.getString(API.key.PID));
            postData.put(API.key.ACCESS_TOKEN, data.getString(API.key.ACCESS_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("ACCESS TOKEN", data.getString(API.key.ACCESS_TOKEN));

        HTTPUtils.doSocialAuthentication(ParentActivity.this, postData, API.SOCIAL_LOGIN_API, new ResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "executing authenticateUser: onSuccess");
                hideLoader();
                afterSuccessfullLogin();
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing authenticateUser: onException");
                hideLoader();
            }

            @Override
            public void onError(String Error) {
                Log.d(TAG, "executing authenticateUser: onError");
                hideLoader();
                String errorMsg = ResponseParser.getSocialAuthError(Error);
                ((ParentActivity) ParentActivity.this).showErrorMsg(errorMsg);
            }
        });

        } else {

            // showLoader(this.getResources().getString(R.string.login_auth_msg));

            if(data == null){
                //TODO: error
            }
            final String emailStr = data.getString(API.key.EMAIL);
            final String passwordStr = data.getString(API.key.PASSWORD);
            final ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("mail", emailStr));
            pairs.add(new BasicNameValuePair("password", passwordStr));

            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    Header[] headers = null;
                    try {
                        headers = HTTPUtils.getHTTPHeaders(pairs, API.LOGIN_API);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        // hideLoader();
                        if (e instanceof IOException)
                            //  showErrorMsg("Please check your network connection");
                            return;
                    }

                    //hideLoader();
                    if (headers != null) {
                        for (Header header : headers) {
                            String key = header.getName();
                            if (key != null && (key.trim().equals(API.key.XR_MAIL) || key.trim().equals(API.key.XR_AUTH))) {
                                String value = header.getValue();
                                KeyValue.insertKeyValue(ParentActivity.this, key, value);
                            }
                        }
                    }
                    afterSuccessfullLogin();
                }
            }.start();

        }

    }

    private void afterSuccessfullLogin() {
        Log.d(TAG, "executing afterSuccessfullLogin");
        if (UserUtils.isValidAppUser()) {
            launchHomeScreen();
            ReceiptUtils.fetchReceiptsAndSave();
        } else {
            showErrorMsg("Login Failed !!!");
        }
    }

    private void launchHomeScreen() {
        Log.d(TAG, "executing launchHomeScreen");
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                finish();
                startActivity(new Intent(ParentActivity.this, HomePageActivity.class));
                ParentActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        });

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
