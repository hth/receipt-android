package com.receiptofi.receiptapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.RequestBody;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Omkar Gharat on 11/20/2016.
 */

public class LogInActivity1 extends ParentActivity implements View.OnClickListener {

    private static final String TAG = LogInActivity1.class.getSimpleName();
    private StringBuilder errors = new StringBuilder();
    private Toolbar toolbar;
    private EditText email,password;
    private  String emailStr,passwordStr;
    private Button forgotPasswordButton,logIn;
    private LinearLayout fbLogin,googleLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.login_page1);

        toolbar = (Toolbar)findViewById(R.id.logintoolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        forgotPasswordButton = (Button)findViewById(R.id.forgot_password_button);
        logIn = (Button)findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        fbLogin = (LinearLayout)findViewById(R.id.facebook_login);
        googleLogin = (LinearLayout)findViewById(R.id.google_login);
        fbLogin.setOnClickListener(this);
        googleLogin.setOnClickListener(this);
        logIn.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity1.this,PasswordRecoveryActivity1.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                logIn.setEnabled(areFieldsSet());
            }
        };


        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
    }

    private boolean areFieldsSet() {
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        return emailStr.length() >= Validation.EMAIL_MIN_LENGTH && passwordStr.length() >= Validation.PASSWORD_MIN_LENGTH;
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "executing onClick");
        switch (view.getId()) {
            case R.id.login_button:
                Log.d(TAG, "login_button clicked");
                login();
                break;
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

    private void login() {
        /** Hide soft keyboard. */
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        /** Getting email and password and trim email. */
        emailStr = email.getText().toString().trim();
        passwordStr = password.getText().toString();

        /** Validation. */
        if (TextUtils.isEmpty(emailStr)) {
            addErrorMsg(getResources().getString(R.string.err_str_enter_email));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(getResources().getString(R.string.err_str_enter_valid_email));
            }
        }
        if (TextUtils.isEmpty(passwordStr)) {
            addErrorMsg(getResources().getString(R.string.err_str_enter_password));
        }

        /** Error string is for keeping the error that needs to be shown to the user. */
        if (errors.length() > 0) {
            showToast(errors.toString(), SuperToast.Duration.MEDIUM, SuperToast.Background.RED);
            errors.delete(0, errors.length());
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(API.key.SIGNIN_EMAIL, emailStr);
            bundle.putString(API.key.SIGNIN_PASSWORD, passwordStr);
            authenticateLogIn(bundle);
        }
    }

    private void authenticateLogIn(Bundle data) {
        Log.d(TAG, "Authenticating");
        showLoader(getResources().getString(R.string.login_auth_msg));
        logIn.setEnabled(false);

        RequestBody formBody = new FormEncodingBuilder()
                .add(API.key.SIGNIN_EMAIL, data.getString(API.key.SIGNIN_EMAIL))
                .add(API.key.SIGNIN_PASSWORD, data.getString(API.key.SIGNIN_PASSWORD))
                .build();

        ExternalCallWithOkHttp.authenticate(LogInActivity1.this, formBody, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "Executing authenticateLogIn: onSuccess");
                Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                saveAuthKey(ExternalCallWithOkHttp.parseHeader(headers, keys));
                afterSuccessfulLogin();
                hideLoader();
                finish();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "Executing authenticateLogIn: onError: " + error);
                hideLoader();
                if (JsonParseUtils.isJSONValid(error)) {
                    showToast(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.LONG, SuperToast.Background.RED);
                } else {
                    showToast("Login failed. Either user does not exists or invalid password.", SuperToast.Duration.LONG, SuperToast.Background.RED);
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        logIn.setEnabled(true);
                    }
                });
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "Executing authenticateLogIn: onException: " + exception.getMessage());
                hideLoader();
                showToast(exception.getMessage(), SuperToast.Duration.LONG, SuperToast.Background.RED);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        logIn.setEnabled(true);
                    }
                });
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

    private void showToast(final String msg, final int length, final int color) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
    }


}
