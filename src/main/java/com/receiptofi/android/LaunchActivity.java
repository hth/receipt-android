package com.receiptofi.android;

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
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.API.key;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.utils.ReceiptUtils;
import com.receiptofi.android.utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Launch activity facilities user to login using either of 4 ways:
 * 1. Facebook Account
 * 2. Google Account
 * 3. Sign up
 * 4. Sign in
 * <p/>
 * This activity won't generated any error message or notifications
 */
public class LaunchActivity extends ParentActivity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = LaunchActivity.class.getSimpleName();

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
        Log.d(TAG, "executing onCreate");
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
        TextView signUp = (TextView) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, HomePageActivity.class));
                finish();
            }
        });

        // Sign in
        TextView signIn = (TextView) findViewById(R.id.sign_in_button);
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
        super.onStart();
        Log.d(TAG, "executing onStart");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "executing onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "executing onResume");
        //addToBackStack(this);

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
        Log.d(TAG, "executing authenticateUser");
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
                Log.d(TAG, "executing authenticateUser: onSuccess");
                afterSuccessfullLogin();
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing authenticateUser: onException");
            }

            @Override
            public void onError(String Error) {
                Log.d(TAG, "executing authenticateUser: onError");
                String errorMsg = ResponseParser.getSocialAuthError(Error);
                ((ParentActivity) LaunchActivity.this).showErrorMsg(errorMsg);
            }
        });

    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "executing onClick");
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
        Log.d(TAG, "executing signInWithGplus");
        Log.d(TAG, "Google Sign in: signInWithGplus: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        if (!mGoogleApiClient.isConnecting()) {
            isGPlusLoginClicked = true;
            resolveSignInError();
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
                startActivity(new Intent(LaunchActivity.this, HomePageActivity.class));
                LaunchActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
            }
        });

    }

    private void resolveSignInError() {
        Log.d(TAG, "executing resolveSignInError");
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
        Log.d(TAG, "executing onActivityResult");
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
        Log.d(TAG, "executing onConnectionFailed");
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
        Log.d(TAG, "executing onConnected");
        Log.d(TAG, "Google Sign in: signInWithGplus: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        isGPlusLoginClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getUserInformation();
    }

    /*
    private void signOutFromGplus() {
        Log.d(TAG, "executing signOutFromGplus");
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }
    */

    private void getUserInformation() {
        Log.d(TAG, "executing getUserInformation");
        AccessTokenGooglePlus gToken = new AccessTokenGooglePlus();
        gToken.execute((Void) null);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "executing onConnectionSuspended");
        mGoogleApiClient.connect();

    }

    public class AccessTokenGooglePlus extends AsyncTask<Void, Void, Void> {

        String token = null;
        String scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
        String scopes = "audience:server:client_id:" + API.key.SERVER_CLIENT_ID;

        @Override
        protected Void doInBackground(Void... params) {
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
