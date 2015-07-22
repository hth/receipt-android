package com.receiptofi.checkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.Validation;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.RequestBody;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * User: PT
 * Date: 12/7/14 10:23 AM
 */
public class LogInActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = LogInActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private EditText password;
    private String emailStr;
    private String passwordStr;

    private boolean isLeftButtonClicked = false;
    private boolean isRightButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

        final TextView logIn = (TextView) findViewById(R.id.login_button);
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

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(textWatcher);

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

        if (!"debug".equals(BuildConfig.BUILD_TYPE)) {
            Button left_bt = (Button) findViewById(R.id.hidden_left_Button);
            left_bt.setVisibility(View.INVISIBLE);

            Button right_bt = (Button) findViewById(R.id.hidden_right_Button);
            right_bt.setVisibility(View.INVISIBLE);
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
        isLeftButtonClicked = false;
        isRightButtonClicked = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * We add this process.
         * Because the loader will be destoried during Orientation changing.
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

    public void leftButtonClick(View view) {
        Log.d(TAG, "inside leftButtonClick");
        isLeftButtonClicked = true;
        if (isRightButtonClicked) {
            email.setText("li@receiptofi.com");
            password.setText("Chongzhi");
        }
    }

    public void rightButtonClick(View view) {
        Log.d(TAG, "inside rightButtonClick");
        isRightButtonClicked = true;
        if (isLeftButtonClicked) {
            email.setText("blank@r.com");
            password.setText("testtest");
        }
    }

    private boolean areFieldsSet() {
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        return emailStr.length() >= Validation.EMAIL_MIN_LENGTH && passwordStr.length() >= Validation.PASSWORD_MIN_LENGTH;
    }

    private void login() {
        // Hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        // getting username and password
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(emailStr)) {
            errors.append(this.getResources().getString(R.string.err_str_enter_email));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(this.getResources().getString(R.string.err_str_enter_valid_email));
            }
        }
        if (TextUtils.isEmpty(passwordStr)) {
            addErrorMsg(this.getResources().getString(R.string.err_str_enter_password));
        }
        // error string is for keeping the error that needs to be shown to the
        // user.
        if (errors.length() > 0) {
            SuperActivityToast superActivityToast = new SuperActivityToast(LogInActivity.this);
            superActivityToast.setText(errors);
            superActivityToast.setDuration(SuperToast.Duration.SHORT);
            superActivityToast.setBackground(SuperToast.Background.BLUE);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();

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
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        RequestBody formBody = new FormEncodingBuilder()
                .add(API.key.SIGNIN_EMAIL, data.getString(API.key.SIGNIN_EMAIL))
                .add(API.key.SIGNIN_PASSWORD, data.getString(API.key.SIGNIN_PASSWORD))
                .build();

        ExternalCallWithOkHttp.authenticate(LogInActivity.this, formBody, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "Executing authenticateLogIn: onSuccess");
                Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                saveAuthKey(ExternalCallWithOkHttp.parseHeader(headers, keys));
                hideLoader();
                afterSuccessfulLogin();
                finish();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "Executing authenticateLogIn: onError: " + error);
                hideLoader();
                if (TextUtils.isEmpty(error)) {
                    showErrorMsg("Login failed.", SuperToast.Duration.LONG);
                } else {
                    showErrorMsg(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.LONG);
                }
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "Executing authenticateLogIn: onException: " + exception.getMessage());
                hideLoader();
                showErrorMsg(exception.getMessage(), SuperToast.Duration.LONG);
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
}
