package com.receiptofi.receiptapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Headers;

/**
 * Created by Omkar Gharat on 11/19/2016.
 */

public class SignUpActivity1 extends ParentActivity implements View.OnClickListener {

    private static final String TAG = SignUpActivity1.class.getSimpleName();

    private Toolbar toolbar;

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText age;
    private TextView signupTerms;
    private Button signUp;
    private LinearLayout fbLogin , googlLogin;

    private String nameStr,passwordStr,emailStr,ageRange;
    private StringBuilder errors = new StringBuilder();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page1);
        toolbar = (Toolbar)findViewById(R.id.signuptoolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText)findViewById(R.id.name);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        age= (EditText)findViewById(R.id.age);
        signUp = (Button)findViewById(R.id.sign_up_button);
        fbLogin = (LinearLayout)findViewById(R.id.facebook_login);
        googlLogin = (LinearLayout)findViewById(R.id.google_login);

        signUp.setOnClickListener(this);
        fbLogin.setOnClickListener(this);
        googlLogin.setOnClickListener(this);
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
                signUp.setEnabled(areFieldsSet());
            }
        };
        name.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        age.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View v) {
     switch (v.getId())
     {
         case R.id.sign_up_button:
             signUp();
             break;
         case R.id.facebook_login:
             Log.d(TAG, "facebook_login clicked");
             isFbLoginClicked = true;
             openFacebookSession();
             break;
         case R.id.google_login:
             Log.d(TAG, "google_login clicked");
             isGPlusLoginClicked = true;
             signInWithGplus();
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
        String agestr = age.getText().toString();
        int ageRange = 0;
        if(agestr!=null)
        {
            if(!
                    agestr.isEmpty())
             ageRange =  Integer.valueOf(agestr);
        }

        return nameStr.length() >= Validation.NAME_MIN_LENGTH &&
                emailStr.length() >= Validation.EMAIL_MIN_LENGTH &&
                passwordStr.length() >= Validation.PASSWORD_MIN_LENGTH&&
                ageRange>0;
    }

    private void signUp() {
        /** Hide soft keyboard. */
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        /** Getting email and password and trim email. */
        nameStr = name.getText().toString();
        emailStr = email.getText().toString().trim();
        passwordStr = password.getText().toString();
        //---Change by me
        ageRange = age.getText().toString();
        /** Validation. */
        if (TextUtils.isEmpty(nameStr)) {
            addErrorMsg(getResources().getString(R.string.err_str_enter_name));
        }
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
            showToast(errors.toString(), SuperToast.Duration.EXTRA_LONG, SuperToast.Background.RED);
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

    private void showToast(final String msg, final int length, final int color) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperToast superToast = new SuperToast(SignUpActivity1.this);
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

    private void addErrorMsg(String msg) {
        if (errors.length() == 0) {
            errors.append(msg);
        } else {
            errors.append("\n").append("\n").append(msg);
        }
    }


    private void authenticateSignUp(Bundle data) {
        Log.d(TAG, "executing authenticateSignUp");
        showLoader(getResources().getString(R.string.sign_up_msg));
        signUp.setEnabled(false);

        JSONObject postData = new JSONObject();

        try {
            postData.put(API.key.SIGNUP_FIRSTNAME, data.getString(API.key.SIGNUP_FIRSTNAME));
            postData.put(API.key.SIGNUP_EMAIL, data.getString(API.key.SIGNUP_EMAIL));
            postData.put(API.key.SIGNUP_PASSWORD, data.getString(API.key.SIGNUP_PASSWORD));
            postData.put(API.key.SIGNUP_AGE, data.getString(API.key.SIGNUP_AGE));
        } catch (JSONException e) {
            Log.e(TAG, "Exception while adding postdata: " + e.getMessage(), e);
        }

        ExternalCallWithOkHttp.doPost(SignUpActivity1.this, postData, API.SIGNUP_API, IncludeAuthentication.NO, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "executing authenticateSignUp: onSuccess");
                Set<String> keys = new HashSet<>(Arrays.asList(API.key.XR_MAIL, API.key.XR_AUTH));
                saveAuthKey(ExternalCallWithOkHttp.parseHeader(headers, keys));
                afterSuccessfulLogin();
                hideLoader();
                finish();
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing authenticateSignUp: onError: " + error);
                hideLoader();
                showToast(JsonParseUtils.parseForErrorReason(error), SuperToast.Duration.LONG, SuperToast.Background.RED);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        signUp.setEnabled(true);
                    }
                });
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing authenticateSignUp: onException: " + exception.getMessage());
                hideLoader();
                showToast(exception.getMessage(), SuperToast.Duration.LONG, SuperToast.Background.RED);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        signUp.setEnabled(true);
                    }
                });
            }
        });
    }

}
