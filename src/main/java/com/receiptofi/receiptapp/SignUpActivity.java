package com.receiptofi.receiptapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;
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
    private ImageView nameImage;
    private EditText email;
    private ImageView emailImage;
    private EditText password;
    private ImageView passwordImage;
    private Spinner ageSpinner;
    private TextView signupTerms;
    private TextView signUp;

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

        signUp = (TextView) findViewById(R.id.sign_up_button);
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

        nameImage = (ImageView) findViewById(R.id.name_image);
        nameImage.setImageDrawable(new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_user)
                .colorRes(R.color.white)
                .actionBarSize());

        name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(textWatcher);

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

        ageSpinner = (Spinner) findViewById(R.id.age_spinner);
        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, "Selected range is: " + ageRange);
                if (position == 0) {
                    ageRange = "";
                    return;
                }
                ageRange = (String) adapterView.getItemAtPosition(position);
                if (ageRange.startsWith("Above")) {
                    ageRange = ageRange.split(" ")[1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        signupTerms = (TextView) findViewById(R.id.signup_term_id);
        String linkText = getResources().getString(R.string.signup_term);
        signupTerms.setText(Html.fromHtml(linkText));
        signupTerms.setMovementMethod(LinkMovementMethod.getInstance());

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
        /** Hide soft keyboard. */
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        /** Getting email and password and trim email. */
        nameStr = name.getText().toString();
        emailStr = email.getText().toString().trim();
        passwordStr = password.getText().toString();

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

        ExternalCallWithOkHttp.doPost(SignUpActivity.this, postData, API.SIGNUP_API, IncludeAuthentication.NO, new ResponseHandler() {

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
                SuperToast superToast = new SuperToast(SignUpActivity.this);
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
