package com.receiptofi.checkout;

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
import com.receiptofi.checkout.http.HTTPUtils;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by PT on 12/8/14.
 */
public class SignUpActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText age;

    private String nameStr;
    private String emailStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        final TextView signUp = (TextView) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(this);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUp.setEnabled(areFieldsSet());
            }
        };

        name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(textWatcher);

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(textWatcher);

        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(textWatcher);

        LinearLayout facebooklogin = (LinearLayout) findViewById(R.id.facebook_login);
        facebooklogin.setOnClickListener(this);

        LinearLayout googlelogin = (LinearLayout) findViewById(R.id.google_login);
        googlelogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "executing onClick");
        switch (view.getId()) {
            case R.id.sign_up_button:
                signUp();
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
        nameStr = name.getText().toString();
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();
        return nameStr.length() >= Constants.NAME_MIN_LENGTH && emailStr.length() >= Constants.EMAIL_MIN_LENGTH
                && passwordStr.length() >= Constants.PASSWORD_MIN_LENGTH;
    }

    private void signUp() {
        // getting username and password
        nameStr = name.getText().toString();
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();

        if (TextUtils.isEmpty(nameStr)) {
            addErrorMsg(this.getResources().getString(R.string.err_str_enter_name));
        }
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
            bundle.putString(API.key.SIGNUP_FIRSTNAME, nameStr);
            bundle.putString(API.key.SIGNUP_EMAIL, emailStr);
            bundle.putString(API.key.SIGNUP_PASSWORD, passwordStr);
            //TODO
            bundle.putString(API.key.SIGNUP_AGE, "");
            authenticateSignUp(bundle);
        }

    }

    private void authenticateSignUp(Bundle data) {
        Log.d(TAG, "executing authenticateSignUp");
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        if (data == null) {
            errors.append(this.getResources().getString(R.string.err_str_bundle_null));
            Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
            return;
        }
        JSONObject postData = new JSONObject();

        try {
            postData.put(API.key.SIGNUP_FIRSTNAME, data.getString(API.key.SIGNUP_FIRSTNAME));
            postData.put(API.key.SIGNUP_EMAIL, data.getString(API.key.SIGNUP_EMAIL));
            postData.put(API.key.SIGNUP_PASSWORD, data.getString(API.key.SIGNUP_PASSWORD));
            postData.put(API.key.SIGNUP_AGE, data.getString(API.key.SIGNUP_AGE));
        } catch (JSONException e) {
            Log.d(TAG, "Exception while adding postdata: " + e.getMessage());
        }

        HTTPUtils.doPost(postData, API.SIGNUP_API, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers) {
                Log.d(TAG, "executing authenticateSignUp: onSuccess");
                Set<String> keys = new HashSet<String>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                Map<String, String> headerData = HTTPUtils.parseHeader(headers, keys);
                saveAuthKey(SignUpActivity.this, headerData);
                hideLoader();
                afterSuccessfulLogin();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing authenticateSignUp: onError" + error);
                hideLoader();
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing authenticateSignUp: onException" + exception.getMessage());
                hideLoader();
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
