package com.receiptofi.receiptapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;


/**
 * User: PT
 * Date: 12/7/14 10:23 AM
 */
public class LogInActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = LogInActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private ImageView emailImage;
    private EditText password;
    private ImageView passwordImage;
    private String emailStr;
    private String passwordStr;
    private TextView buildVersion;
    private TextView logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

        logIn = (TextView) findViewById(R.id.login_button);
        logIn.setOnClickListener(this);

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

        emailImage = (ImageView) findViewById(R.id.email_image);
        emailImage.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_envelope)
                .colorRes(R.color.white)
                .actionBarSize());

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(textWatcher);

        passwordImage = (ImageView) findViewById(R.id.password_image);
        passwordImage.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_lock)
                .colorRes(R.color.white)
                .actionBarSize());

        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(textWatcher);

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password_button);
        /**
         * Add underline of forgot Password to make it looks more hybird link.
         */
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, PasswordRecoveryActivity.class));
            }
        });

        LinearLayout facebookLogin = (LinearLayout) findViewById(R.id.facebook_login);
        facebookLogin.setOnClickListener(this);

        LinearLayout googleLogin = (LinearLayout) findViewById(R.id.google_login);
        googleLogin.setOnClickListener(this);

        if ("release".equals(BuildConfig.BUILD_TYPE)) {
            buildVersion = (TextView) findViewById(R.id.login_build_version);
            buildVersion.setVisibility(View.INVISIBLE);
        } else {
            buildVersion = (TextView) findViewById(R.id.login_build_version);
            buildVersion.setText(BuildConfig.VERSION_NAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * We add below to avoid window link exception during orientation change.
         */
        if (super.loader != null) {
            super.loader.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * We add this process.
         * Because the loader will be destroyed during Orientation changing.
         * And a new Window link exception will throw up.
         */
        if (loginToastRunning) {
            if (super.loader == null) {
                showLoader(this.getResources().getString(R.string.login_auth_msg));
            } else {
                super.loader.show();
            }
        }
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

    private boolean areFieldsSet() {
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        return emailStr.length() >= Validation.EMAIL_MIN_LENGTH && passwordStr.length() >= Validation.PASSWORD_MIN_LENGTH;
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

        RequestBody formBody = new FormBody.Builder()
                .add(API.key.SIGNIN_EMAIL, data.getString(API.key.SIGNIN_EMAIL))
                .add(API.key.SIGNIN_PASSWORD, data.getString(API.key.SIGNIN_PASSWORD))
                .build();

        ExternalCallWithOkHttp.authenticate(LogInActivity.this, formBody, new ResponseHandler() {

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

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperToast superToast = new SuperToast(LogInActivity.this);
                superToast.setText(msg);
                superToast.setDuration(length);
                superToast.setBackground(color);
                superToast.setTextColor(Color.WHITE);
                superToast.setAnimations(SuperToast.Animations.FLYIN);
                superToast.setGravity(Gravity.TOP, 0, 20);
                superToast.show();
            }
        });
    }
}
