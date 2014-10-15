package com.receiptofi.android;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
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

public class LoginActivity extends ParentActivity implements OnClickListener ,ConnectionCallbacks, OnConnectionFailedListener{

	private EditText userName;
    private EditText password;
	private String userNameStr;
    private String passwordStr;

	private StringBuilder errors = new StringBuilder();
	private TextView signupText;
	private static final int GOOGLE_PLUS_SIGN_IN = 0x2565;
	private static final int FACEBOOK_SIGN_IN = 0x2566;
	private GoogleApiClient mGoogleApiClient;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;
	private boolean mIntentInProgress;
	SignInButton googlePlusLogin;
	Button facebookLogin;
	private boolean isFbLoginClick = false;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (UserUtils.isValidAppUser()) {
			finish();
			startActivity(new Intent(this, HomePageActivity.class));
		}
		setContentView(R.layout.login_page);
		userName = (EditText) findViewById(R.id.userName);
		password = (EditText) findViewById(R.id.password);
		signupText = (TextView) findViewById(R.id.signupText);
		
		googlePlusLogin =(SignInButton)findViewById(R.id.loginGooglePlus);

		userName.setOnFocusChangeListener(editTextListener);
		password.setOnFocusChangeListener(editTextListener);
		googlePlusLogin.setOnClickListener(this);
		facebookLogin = (Button)findViewById(R.id.loginFacebook);
		facebookLogin.setOnClickListener(this);
		
		signupText
				.setText(Html
						.fromHtml("Don't have an account? <u><font color=\"blue\">Sign up</font></u>, it's free!"));
		// Initializing google plus api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
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
	
	private static OnFocusChangeListener editTextListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub

			if (hasFocus) {
				((EditText) v).setHint("");
			} else {
				String input = ((EditText) v).getText().toString();
				if (input != null && input.length() == 0) {
					int id = v.getId();
					if (id == R.id.userName) {
						((EditText) v).setHint("Email");
					} else if (id == R.id.password) {
						((EditText) v).setHint("Password");
					}
				}
			}

		}

	};

	public void login(View view) {
		// getting username and password
		userNameStr = userName.getText().toString();
		passwordStr = password.getText().toString();

		if (userNameStr == null	|| (userNameStr != null && userNameStr.length() == 0)) {
			errors.append(this.getResources().getString(R.string.enter_email));
		} else {
			if (!UserUtils.isValidEmail(userNameStr)) {
				addErrorMsg(this.getResources().getString(R.string.enter_valid_email));
			}
		}
		if (passwordStr == null || (passwordStr != null && passwordStr.length() == 0)) {
			addErrorMsg(this.getResources().getString(R.string.enter_password));
		}
		// error string is for keeping the error that needs to be shown to the
		// user.
		if (errors.length() > 0) {
			Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 20);
			toast.show();
			errors.delete(0, errors.length());
		} else {
			authenticateUser(false,null);
		}
	}

	private void launchHomeScreen() {

		uiThread.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				finish();
				startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
				LoginActivity.this.overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
			}
		});

	}

	private void addErrorMsg(String msg) {
		if (errors.length() == 0) {
			errors.append(msg);
		} else {
			errors.append("\n").append("\n").append(msg);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addToBackStack(this);
		
		if(isFbLoginClick){
			isFbLoginClick=false;
			Session s=Session.getActiveSession();
			if(s!=null && s.isOpened()){
				Bundle data = new Bundle();
				data.putString(key.ACCESS_TOKEN, s.getAccessToken());
				data.putString(key.PID, key.PID_FACEBOOK);
				authenticateUser(true, data);
				
			}
		}
		
	}

    private void authenticateUser(boolean isSocialLogin,Bundle data) {
    	if(isSocialLogin){

            showLoader(this.getResources().getString(R.string.login_auth_msg));
            
            JSONObject postData =new JSONObject();
            
            try {
                postData.put(API.key.PID, data.getString(key.PID));
				postData.put(API.key.ACCESS_TOKEN, data.getString(key.ACCESS_TOKEN));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
            Log.i("ACCESS TOKEN", data.getString(key.ACCESS_TOKEN));
       
            HTTPUtils.doSocialAuthentication(LoginActivity.this,postData, API.SOCIAL_LOGIN_API, new ResponseHandler() {
				
				@Override
				public void onSuccess(String response) {
					  afterSuccessfullLogin();
				}
				
				@Override
				public void onExeption(Exception exception) {
					
				}
				
				@Override
				public void onError(String Error) {
					String errorMsg=ResponseParser.getSocialAuthError(Error);
					((ParentActivity)LoginActivity.this).showErrorMsg(errorMsg);
				}
			});
            
            
    	}else {
		
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        final ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("mail", userNameStr));
        pairs.add(new BasicNameValuePair("password", passwordStr));

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                Header[] headers;
                try {
                    headers = HTTPUtils.getHTTPHeaders(pairs, API.LOGIN_API);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    hideLoader();
                    if (e instanceof IOException)
                        showErrorMsg("Please check your network connection");
                    return;
                }

                hideLoader();
                if (headers != null) {
                    for (Header header : headers) {
                        String key = header.getName();
                        if (key != null && (key.trim().equals(API.key.XR_MAIL) || key.trim().equals(API.key.XR_AUTH))) {
                            String value = header.getValue();
                            KeyValue.insertKeyValue(LoginActivity.this, key, value);
                        }
                    }
                }
                afterSuccessfullLogin();
            }
			}.start();

		}   	
    	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
        case R.id.loginGooglePlus:
            // Signin button clicked
            signInWithGplus();
            break;
        case R.id.loginFacebook:
        	isFbLoginClick=true;
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
	    if (!mGoogleApiClient.isConnecting()) {
	        mSignInClicked = true;
	        resolveSignInError();
	    }
	}
	 
	private void afterSuccessfullLogin(){
        if (UserUtils.isValidAppUser()) {
            launchHomeScreen();
            ReceiptUtils.fetchReceiptsAndSave();
        } else {
            showErrorMsg("Login Failed !!!");
        }
	}

	private void resolveSignInError() {
	    if (mConnectionResult.hasResolution()) {
	        try {
	            mIntentInProgress = true;
	            mConnectionResult.startResolutionForResult(this, GOOGLE_PLUS_SIGN_IN);
	        } catch (SendIntentException e) {
	            mIntentInProgress = false;
	            mGoogleApiClient.connect();
	        }
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GOOGLE_PLUS_SIGN_IN) {
	        if (resultCode != RESULT_OK) {
	            mSignInClicked = false;
	        }
	 
	        mIntentInProgress = false;
	 
	        if (!mGoogleApiClient.isConnecting()) {
	            mGoogleApiClient.connect();
	        }
	    }else {
	    	 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		 if (!result.hasResolution()) {
		        GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
		                0).show();
		        return;
		    }
		 
		    if (!mIntentInProgress) {
		        // Store the ConnectionResult for later usage
		        mConnectionResult = result;
		 
		        if (mSignInClicked) {
		            // The user has already clicked 'sign-in' so we attempt to
		            // resolve all
		            // errors until the user is signed in, or they cancel.
		            resolveSignInError();
		        }
		    }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		mSignInClicked = false;
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
		// TODO Auto-generated method stub
		AccessTokenGooglePlus gToken=new AccessTokenGooglePlus();
		gToken.execute((Void)null);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		  mGoogleApiClient.connect();
		  
	}
	
	public class AccessTokenGooglePlus extends AsyncTask<Void,Void,Void> {
		
		String token = null;
		String scope= Scopes.PLUS_LOGIN + " "+Scopes.PLUS_ME;
		String scopes = "audience:server:client_id:" + API.key.SERVER_CLIENT_ID ;
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				// We can retrieve the token to check via
				// tokeninfo or to pass to a service-side
				// application.
				token = GoogleAuthUtil.getToken(LoginActivity.this,Plus.AccountApi.getAccountName(mGoogleApiClient), scopes);
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
			

			if(token!=null){
				Log.i("TOKEN IS NOT NULL MAKING QUERY", "TOKEN IS NOT NULL MAKING QUERY");
				Bundle data=new Bundle();
				data.putString(key.PID, key.PID_GOOGLE);
				data.putString(key.ACCESS_TOKEN, token);
				authenticateUser(true, data);
			}else {
				Log.i("TOKEN IS  NULL MAKING QUERY", "TOKEN IS  NULL MAKING QUERY");
			}
		}
		
		
	}
	
}
