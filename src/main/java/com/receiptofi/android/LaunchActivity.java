package com.receiptofi.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.receiptofi.android.utils.UserUtils;

/**
 * Launch activity facilities user to login using either of 4 ways:
 * 1. Facebook Account
 * 2. Google Account
 * 3. Sign up
 * 4. Sign in
 * <p/>
 * This activity won't generated any error message or notifications
 */
public class LaunchActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = "SUMAN"; //LaunchActivity.class.getSimpleName();


    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;

    private boolean isGPlusLoginClicked = false;
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
        LinearLayout mFacebookLogin = (LinearLayout) findViewById(R.id.facebook_login);
        mFacebookLogin.setOnClickListener(this);

        // login via Google
        LinearLayout mGooglePlusLogin = (LinearLayout) findViewById(R.id.google_login);
        mGooglePlusLogin.setOnClickListener(this);

        // Sign up
        TextView signUp = (TextView) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, LogInActivity.class));
                finish();
            }
        });

        // Sign in
        TextView signIn = (TextView) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, SignUpActivity.class));
                finish();
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
            case R.id.facebook_login:
                Log.d(TAG, "facebook_login clicked");

                isFbLoginClicked = true;
                openFacebookSession();
                break;
            default:
                Log.d(TAG, "done executing onClick no id match");
                break;

        }
    }
}
