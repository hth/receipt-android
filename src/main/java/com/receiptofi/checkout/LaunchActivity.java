package com.receiptofi.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.receiptofi.checkout.utils.UserUtils;

/**
 * Launch activity facilities.
 * User logs in using either of the four ways listed below:
 * 1. Facebook Account
 * 2. Google Account
 * 3. Sign up
 * 4. Sign in
 * <p/>
 * This activity won't generated any error message or notifications
 */
public class LaunchActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = LaunchActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserUtils.isValidAppUser()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        Log.d(TAG, "executing onCreate");
        setContentView(R.layout.launch_page);
        getActionBar().hide();

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
                startActivity(new Intent(LaunchActivity.this, SignUpActivity.class));
            }
        });

        // Sign in
        TextView signIn = (TextView) findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LaunchActivity.this, LogInActivity.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "executing onClick");
        switch (v.getId()) {
            case R.id.google_login:
                Log.d(TAG, "google_login clicked");
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
