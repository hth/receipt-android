package com.receiptofi.android;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.utils.ReceiptUtils;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParentActivity extends Activity {

    private static final String TAG = "SUMAN"; //ParentActivity.class.getSimpleName();

    protected boolean isFbLoginClicked = false;

    protected static final int GOOGLE_PLUS_SIGN_IN = 0x2565;
    protected static final int FACEBOOK_SIGN_IN = 0x2566;


    private static ArrayList<Activity> backStack;
    protected Handler uiThread = new Handler();
    protected Calendar calender = Calendar.getInstance();
    private ProgressDialog loader;

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

    protected void addToBackStack(Activity activity) {
        if (backStack == null) {
            backStack = new ArrayList<Activity>();
        }

        backStack.add(activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Parent executing onResume");

        if (isFbLoginClicked) {
            isFbLoginClicked = false;
            getFBSessionInfo();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Parent executing onActivityResult");

        if (requestCode != GOOGLE_PLUS_SIGN_IN) {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
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

    protected void authenticateSocialAccount(Bundle data) {
        Log.d(TAG, "Parent executing authenticateSocialAccount");
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        JSONObject postData = new JSONObject();

        try {
            postData.put(API.key.PID, data.getString(API.key.PID));
            postData.put(API.key.ACCESS_TOKEN, data.getString(API.key.ACCESS_TOKEN));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.i("ACCESS TOKEN", data.getString(API.key.ACCESS_TOKEN));

        //HTTPUtils.doSocialAuthentication(ParentActivity.this, postData, API.SOCIAL_LOGIN_API, new ResponseHandler() {
        HTTPUtils.doPost(postData, API.SOCIAL_LOGIN_API, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onSuccess");
                Set<String> keys = new HashSet<String>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                Map<String, String> headerData = HTTPUtils.parseHeader(headers, keys);
                saveAuthKey(ParentActivity.this, headerData);
                hideLoader();
                afterSuccessfullLogin();
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onException");
                hideLoader();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onError");
                hideLoader();
                String errorMsg = ResponseParser.getSocialAuthError(error);
                (ParentActivity.this).showErrorMsg(errorMsg);
            }
        });

    }

    protected void afterSuccessfullLogin() {
        Log.d(TAG, "Parent executing afterSuccessfullLogin");
        if (UserUtils.isValidAppUser()) {
            launchHomeScreen();
            ReceiptUtils.fetchReceiptsAndSave();
        } else {
            showErrorMsg("Login Failed !!!");
        }
    }

    private void launchHomeScreen() {
        Log.d(TAG, "Parent executing launchHomeScreen");
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(ParentActivity.this, HomePageActivity.class));
                ParentActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                finish();
            }
        });

    }

    protected void saveAuthKey(Context context, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            boolean success = KeyValue.insertKeyValue(context, entry.getKey(), entry.getValue());
            if(!success){
                Log.e(TAG, "Error while saving Auth data: key is:  " + entry.getKey() + "  value is:  " + entry.getValue());
            }
        }
    }

    public void showErrorMsg(final String msg) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ParentActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

        });
    }

    /***********    for Facebook login    ************/

    protected void openFacebookSession(){
        Log.d(TAG, "Parent executing openFacebookSession");
        openActiveSession(this, true, Arrays.asList("email", "public_profile"), new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d(TAG, "Session status callback");
                if (exception != null) {
                    Log.d(TAG, "Exception is:  " + exception.getMessage());
                    isFbLoginClicked = false;
                    return;
                }
                Log.d(TAG, "Session State: " + session.getState());
                // if(isFbLoginClicked && session != null && session.isOpened()){
                //     getFBSessionInfo();
                //     return;
                //}
            }
        });
    }

    private static Session openActiveSession(Activity activity, boolean allowLoginUI, List permissions, Session.StatusCallback callback) {
        Log.d(TAG, "Parent executing openActiveSession");
        Session.OpenRequest openRequest = new Session.OpenRequest(activity).setPermissions(permissions).setCallback(callback);
        Session session = new Session.Builder(activity).build();
        if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState()) || allowLoginUI) {
            Session.setActiveSession(session);
            session.openForRead(openRequest);
            return session;
        }
        return null;
    }

    protected void getFBSessionInfo(){
        Log.d(TAG, "Parent executing getFBSessionInfo");
        Session s = Session.getActiveSession();
        if (s != null && s.isOpened()) {
            Bundle data = new Bundle();
            data.putString(API.key.ACCESS_TOKEN, s.getAccessToken());
            data.putString(API.key.PID, API.key.PID_FACEBOOK);
            s.closeAndClearTokenInformation();
            authenticateSocialAccount(data);
        }
    }
}
