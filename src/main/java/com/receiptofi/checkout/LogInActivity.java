package com.receiptofi.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.utils.Validation;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by PT on 12/7/14.
 */
public class LogInActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = LogInActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private EditText password;
    private String emailStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

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
        forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, PasswordRecoveryActivity.class));
            }
        });

        LinearLayout facebooklogin = (LinearLayout) findViewById(R.id.facebook_login);
        facebooklogin.setOnClickListener(this);

        LinearLayout googlelogin = (LinearLayout) findViewById(R.id.google_login);
        googlelogin.setOnClickListener(this);

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
            Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(API.key.SIGNIN_EMAIL, emailStr);
            bundle.putString(API.key.SIGNIN_PASSWORD, passwordStr);
            authenticateLogIn(bundle);
        }
    }

    private void authenticateLogIn(Bundle data) {
        Log.d(TAG, "executing authenticateLogIn");
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        if (data == null) {
            errors.append(this.getResources().getString(R.string.err_str_bundle_null));
            Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
            return;
        }
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        pairs.add(new BasicNameValuePair(API.key.SIGNIN_EMAIL, data.getString(API.key.SIGNIN_EMAIL)));
        pairs.add(new BasicNameValuePair(API.key.SIGNIN_PASSWORD, data.getString(API.key.SIGNIN_PASSWORD)));

        ExternalCall.doPost(pairs, API.LOGIN_API, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers, String body) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onSuccess");
                Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                Map<String, String> headerData = ExternalCall.parseHeader(headers, keys);
                saveAuthKey(LogInActivity.this, headerData);
                hideLoader();
                afterSuccessfulLogin();
                finish();
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onException");
                hideLoader();
                showErrorMsg("Please check your network connection");

            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "Parent executing authenticateSocialAccount: onError");
                hideLoader();
                showErrorMsg(error);
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