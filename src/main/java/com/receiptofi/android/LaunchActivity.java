package com.receiptofi.android;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.API.key;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.utils.ReceiptUtils;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.Session;
import com.facebook.SessionState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 *  Launch activity facilities user to login using either of 4 ways:
 *  1. Facebook Account
 *  2. Google Account
 *  3. Sign up
 *  4. Sign in
 *
 *  This activity won't generated any error message or notifications
 */
public class LaunchActivity extends ParentActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "Testing"; //LaunchActivity.class.getSimpleName();

    private static final int GOOGLE_PLUS_SIGN_IN = 0x2565;
    private static final int FACEBOOK_SIGN_IN = 0x2566;



    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private LinearLayout mGooglePlusLogin;
    private LinearLayout mFacebookLogin;
    private boolean isFbLoginClicked = false;
    private boolean isGPlusLoginClicked;
    private boolean mIntentInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (UserUtils.isValidAppUser()) {
            startActivity(new Intent(this, HomePageActivity.class));
            finish();
        }
        setContentView(R.layout.launch_page);


        //login via Facebook
        mFacebookLogin = (LinearLayout) findViewById(R.id.facebook_login);
        mFacebookLogin.setOnClickListener(this);

        // login via Google
        mGooglePlusLogin = (LinearLayout) findViewById(R.id.google_login);
        mGooglePlusLogin.setOnClickListener(this);
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        // Sign up
        TextView signUp = (TextView)findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, HomePageActivity.class));
                finish();
            }
        });

        // Sign in
        TextView signIn = (TextView)findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, HomePageActivity.class));
                finish();
            }
        });

    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addToBackStack(this);

        if (isFbLoginClicked) {
            isFbLoginClicked = false;
            Session s = Session.getActiveSession();
            if (s != null && s.isOpened()) {
                Bundle data = new Bundle();
                data.putString(key.ACCESS_TOKEN, s.getAccessToken());
                data.putString(key.PID, key.PID_FACEBOOK);
                authenticateUser(data);
            }
        }

    }

    private void authenticateUser(Bundle data) {
        showLoader(this.getResources().getString(R.string.login_auth_msg));

            JSONObject postData = new JSONObject();

            try {
                postData.put(API.key.PID, data.getString(key.PID));
                postData.put(API.key.ACCESS_TOKEN, data.getString(key.ACCESS_TOKEN));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.i("ACCESS TOKEN", data.getString(key.ACCESS_TOKEN));

            HTTPUtils.doSocialAuthentication(LaunchActivity.this, postData, API.SOCIAL_LOGIN_API, new ResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    afterSuccessfullLogin();
                }

                @Override
                public void onException(Exception exception) {

                }

                @Override
                public void onError(String Error) {
                    String errorMsg = ResponseParser.getSocialAuthError(Error);
                    ((ParentActivity) LaunchActivity.this).showErrorMsg(errorMsg);
                }
            });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.google_login:
                isGPlusLoginClicked = true;
                signInWithGplus();
                break;
            case R.id.google_logo:
                isGPlusLoginClicked = true;
                signInWithGplus();
                break;
            case R.id.facebook_login:

                isFbLoginClicked = true;
                Session.openActiveSession(this, true, new Session.StatusCallback() {

                    // callback when session changes state
                    @Override
                    public void call(Session session, SessionState state,
                                     Exception exception) {
                        if (session.isOpened()) {

                        }
                    }
                });

                break;
            case R.id.facebook_logo:
                isFbLoginClicked = true;
                Session.openActiveSession(this, true, new Session.StatusCallback() {

                    // callback when session changes state
                    @Override
                    public void call(Session session, SessionState state,
                                     Exception exception) {
                        if (session.isOpened()) {

                        }
                    }
                });

                break;

        }
    }

    private void signInWithGplus() {
        Log.d(TAG, "Google Sign in: signInWithGplus: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        if (!mGoogleApiClient.isConnecting()) {
            isGPlusLoginClicked = true;
            resolveSignInError();
        }
    }

    private void afterSuccessfullLogin() {
        if (UserUtils.isValidAppUser()) {
            launchHomeScreen();
            ReceiptUtils.fetchReceiptsAndSave();
        } else {
            showErrorMsg("Login Failed !!!");
        }
    }

    private void launchHomeScreen() {

        uiThread.post(new Runnable() {

            @Override
            public void run() {
                finish();
                startActivity(new Intent(LaunchActivity.this, HomePageActivity.class));
                LaunchActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        });

    }

    private void resolveSignInError() {
       /* if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, GOOGLE_PLUS_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
       */
        Log.d(TAG, "Google Sign in: resolveSignInError: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());

        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        GOOGLE_PLUS_SIGN_IN, null, 0, 0, 0);
            } catch (SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                Log.e(TAG, "The intent was canceled before it was sent");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Google Sign in: onActivityResult: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());

        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_PLUS_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                isGPlusLoginClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Google Sign in: onConnectionFailed: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (isGPlusLoginClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Google Sign in: signInWithGplus: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        isGPlusLoginClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getUserInformation();
    }

    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    private void getUserInformation() {
        AccessTokenGooglePlus gToken = new AccessTokenGooglePlus();
        gToken.execute((Void) null);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();

    }

    public class AccessTokenGooglePlus extends AsyncTask<Void, Void, Void> {

        String token = null;
        String scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
        String scopes = "audience:server:client_id:" + API.key.SERVER_CLIENT_ID;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                // We can retrieve the token to check via
                // tokeninfo or to pass to a service-side
                // application.
                token = GoogleAuthUtil.getToken(LaunchActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scopes);
            } catch (Exception e) {
                // This error is recoverable, so we could fix this
                // by displaying the intent to the user.
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            if (token != null) {
                Log.i("TOKEN IS NOT NULL MAKING QUERY", "TOKEN IS NOT NULL MAKING QUERY");
                Bundle data = new Bundle();
                data.putString(key.PID, key.PID_GOOGLE);
                data.putString(key.ACCESS_TOKEN, token);
                authenticateUser(data);
            } else {
                Log.i("TOKEN IS  NULL MAKING QUERY", "TOKEN IS  NULL MAKING QUERY");
            }
        }


    }

}
