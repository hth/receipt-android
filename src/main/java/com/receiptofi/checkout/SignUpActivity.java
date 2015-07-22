package com.receiptofi.checkout;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.Validation;
import com.receiptofi.checkout.views.ToastBox;
import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: PT
 * Date: 12/8/14 7:34 AM
 */
public class SignUpActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText name;
    private EditText email;
    private EditText password;
    private Spinner ageSpinner;

    private String nameStr;
    private String emailStr;
    private String passwordStr;
    private String ageRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVerticalScrollBarEnabled(false);

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

        ageSpinner = (Spinner) findViewById(R.id.age_spinner);
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    ageRange = "";
                    Log.d(TAG, "Selected range is: " + ageRange);
                    return;
                }
                ageRange = (String) adapterView.getItemAtPosition(position);
                Log.d(TAG, "Selected range is: " + ageRange);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        LinearLayout facebookLogin = (LinearLayout) findViewById(R.id.facebook_login);
        facebookLogin.setOnClickListener(this);

        LinearLayout googleLogin = (LinearLayout) findViewById(R.id.google_login);
        googleLogin.setOnClickListener(this);
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
        return nameStr.length() >= Validation.NAME_MIN_LENGTH &&
                emailStr.length() >= Validation.EMAIL_MIN_LENGTH &&
                passwordStr.length() >= Validation.PASSWORD_MIN_LENGTH;
    }

    private void signUp() {
        // Hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

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
            //TODO(hth) fix this with super toast
            Toast toast = ToastBox.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(API.key.SIGNUP_FIRSTNAME, nameStr);
            bundle.putString(API.key.SIGNUP_EMAIL, emailStr);
            bundle.putString(API.key.SIGNUP_PASSWORD, passwordStr);
            bundle.putString(API.key.SIGNUP_AGE, ageRange);
            authenticateSignUp(bundle);
        }

    }

    private void authenticateSignUp(Bundle data) {
        Log.d(TAG, "executing authenticateSignUp");
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        //TODO(hth) fix this with super toast
        if (data == null) {
            errors.append(getResources().getString(R.string.err_str_bundle_null));
            Toast toast = ToastBox.makeText(this, errors, Toast.LENGTH_SHORT);
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
            Log.e(TAG, "Exception while adding postdata: " + e.getMessage(), e);
        }

        ExternalCallWithOkHttp.doPost(SignUpActivity.this, postData, API.SIGNUP_API, IncludeAuthentication.NO, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "executing authenticateSignUp: onSuccess");
                Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                saveAuthKey(ExternalCallWithOkHttp.parseHeader(headers, keys));
                hideLoader();
                afterSuccessfulLogin();
                finish();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing authenticateSignUp: onError: " + error);
                hideLoader();
                showErrorMsg(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.LONG);
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing authenticateSignUp: onException: " + exception.getMessage());
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
