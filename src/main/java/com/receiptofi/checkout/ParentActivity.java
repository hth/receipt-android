package com.receiptofi.checkout;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.receiptofi.checkout.dbutils.KeyValueUtils;
import com.receiptofi.checkout.dbutils.ReceiptUtils;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.HTTPUtils;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.http.ResponseParser;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParentActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    protected static final int GOOGLE_PLUS_SIGN_IN = 0x2565;
    protected static final int FACEBOOK_SIGN_IN = 0x2566;
    private static final String TAG = ParentActivity.class.getSimpleName();
    private static ArrayList<Activity> backStack;
    protected boolean isFbLoginClicked = false;
    protected boolean isGPlusLoginClicked = false;
    protected Handler uiThread = new Handler();
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private boolean mIntentInProgress;
    private ProgressDialog loader;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

    }

    public void startFragment(Fragment fragment, boolean isaddToBackStack, int viewId) {
        // TODO Auto-generated method stub
        getFragmentManager().beginTransaction().replace(viewId, fragment)
                .addToBackStack(null).commit();
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

        if (requestCode == GOOGLE_PLUS_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                isGPlusLoginClicked = false;
                getUserInformation();
            } else {
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnected()) {
                mGoogleApiClient.connect();
            }
        } else {
            if (null != Session.getActiveSession()) {
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
            }
        }
    }

    /**
     * ********    Back-stack for activities    ***********
     */
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

    /**
     * ********    Toast and loader for login status    ***********
     */
    public void showErrorMsg(final String msg) {
        uiThread.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(ParentActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

        });
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

    /**
     * ********    Authenticate social login    ***********
     */
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

        HTTPUtils.doPost(postData, API.SOCIAL_LOGIN_API, false, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onSuccess");
                Set<String> keys = new HashSet<String>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                Map<String, String> headerData = HTTPUtils.parseHeader(headers, keys);
                checkIfUserExist(headerData.get(API.key.XR_MAIL));
                saveAuthKey(ParentActivity.this, headerData);
                hideLoader();
                afterSuccessfulLogin();
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

    protected void checkIfUserExist(String email) {
        if (!UserUtils.userExist(email)) {
            KeyValueUtils.clearKeyValues();
            KeyValueUtils.clearReceiptsDB();
        }
    }

    protected void saveAuthKey(Context context, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            boolean success = KeyValueUtils.insertKeyValue(context, entry.getKey(), entry.getValue());
            if (!success) {
                Log.e(TAG, "Error while saving Auth data: key is:  " + entry.getKey() + "  value is:  " + entry.getValue());
            }
        }
    }

    protected void afterSuccessfulLogin() {
        Log.d(TAG, "Parent executing afterSuccessfulLogin");
        if (UserUtils.isValidAppUser()) {
            launchHomeScreen();
            finish();
            // TODO make this call later
            //ReceiptUtils.getUnprocessCount();

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
                Intent i = new Intent(ParentActivity.this, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * ********    for Facebook login    ***********
     */

    protected void openFacebookSession() {
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

    protected void getFBSessionInfo() {
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

    /**
     * ********    for Google login    ***********
     */

    protected void signInWithGplus() {
        Log.d(TAG, "executing signInWithGplus");
        Log.d(TAG, "Google Sign in: signInWithGplus: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());
        mGoogleApiClient.connect();
        if (!mGoogleApiClient.isConnecting()) {
            resolveSignInError();
        }
    }

    private void resolveSignInError() {
        Log.d(TAG, "executing resolveSignInError");
        Log.d(TAG, "Google Sign in: resolveSignInError: mGoogleApiClient.isConnected(): " + mGoogleApiClient.isConnected());

        if (null != mConnectionResult && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        GOOGLE_PLUS_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                Log.e(TAG, "The intent was canceled before it was sent");
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
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
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "executing onConnectionSuspended");
        mGoogleApiClient.connect();

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

    private void getUserInformation() {
        Log.d(TAG, "executing getUserInformation");
        AccessTokenGooglePlus gToken = new AccessTokenGooglePlus();
        gToken.execute((Void) null);
    }

    /**
     * ********    Async task to fetch Google auth-key    ***********
     */


    public class AccessTokenGooglePlus extends AsyncTask<Void, Void, Void> {

        String token = null;
        //String scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
        //String scopes = "audience:server:client_id:" + API.KEYS.SERVER_CLIENT_ID;

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(TAG, "executing AccessTokenGooglePlus");
            try {
                // We can retrieve the token to check via
                // tokeninfo or to pass to a service-side
                // application.
                //token = GoogleAuthUtil.getToken(LaunchActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), scopes);

                Bundle appActivities = new Bundle();
                appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "");
                String scope = Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME;
                String scopes = "oauth2:server:client_id:" + API.key.SERVER_CLIENT_ID + ":api_scope:" + scope;
                //String scopes = "oauth2:server:client_id:<SERVER-CLIENT-ID>:api_scope:<SCOPE1> <SCOPE2>";

                token = GoogleAuthUtil.getToken(
                        ParentActivity.this,                                              // Context context
                        Plus.AccountApi.getAccountName(mGoogleApiClient),  // String accountName
                        scopes,                                            // String scope
                        appActivities                                      // Bundle bundle
                );
                Log.d(TAG, "token is: " + token);
            } catch (IOException transientEx) {
                // network or server error, the call is expected to succeed if you try again later.
                // Don't attempt to call again immediately - the request is likely to
                // fail, you'll hit quotas or back-off.
                Log.d(TAG, "executing AccessTokenGooglePlus: got IOException: " + transientEx.getMessage());
                return null;
            } catch (UserRecoverableAuthException e) {
                // Requesting an authorization code will always throw
                // UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
                // because the user must consent to offline access to their data.  After
                // consent is granted control is returned to your activity in onActivityResult
                // and the second call to GoogleAuthUtil.getToken will succeed.
                Log.d(TAG, "executing AccessTokenGooglePlus: got UserRecoverableAuthException: " + e.getMessage());
                startActivityForResult(e.getIntent(), GOOGLE_PLUS_SIGN_IN);
                return null;
            } catch (GoogleAuthException authEx) {
                // Failure. The call is not expected to ever succeed so it should not be
                // retried.
                Log.d(TAG, "executing AccessTokenGooglePlus: got GoogleAuthException: " + authEx.getMessage());
                return null;
            } catch (Exception e) {
                Log.d(TAG, "executing AccessTokenGooglePlus: got Exception: " + e.getMessage());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (token != null) {
                Log.i("TOKEN IS NOT NULL MAKING QUERY", "TOKEN IS NOT NULL MAKING QUERY");
                Bundle data = new Bundle();
                data.putString(API.key.PID, API.key.PID_GOOGLE);
                data.putString(API.key.ACCESS_TOKEN, token);
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                }
                authenticateSocialAccount(data);
            } else {
                Log.i("TOKEN IS  NULL MAKING QUERY", "TOKEN IS  NULL MAKING QUERY");
            }
        }
    }

}
