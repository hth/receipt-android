package com.receiptofi.receiptapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.ParentActivity;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.ErrorModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.model.types.MobileSystemErrorCodeEnum;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.UserUtils;
import com.receiptofi.receiptapp.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

/**
 * Created by Omkar Gharat on 11/20/2016.
 */

public class PasswordRecoveryActivity1 extends ParentActivity implements View.OnClickListener {
    protected static final int PASSWORD_RECOVERY_SUCCESS = 0x2965;
    protected static final int PASSWORD_RECOVERY_ERROR = 0x2966;
    protected static final int PASSWORD_RECOVERY_EXCEPTION = 0x2967;

    private  Toolbar toolbar;
    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private Button passwordRecovery;

    private static final String TAG = PasswordRecoveryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recovery1);
        toolbar = (Toolbar)findViewById(R.id.passwordrecoverytoobar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passwordRecovery = (Button) findViewById(R.id.password_recovery_button);
        passwordRecovery.setOnClickListener(this);
        email = (EditText) findViewById(R.id.email);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordRecovery.setEnabled(editable.length() >= Validation.EMAIL_MIN_LENGTH);

               // TextView recoveryStatus = (TextView) findViewById(R.id.password_recovery_info);
                /*if (!TextUtils.isEmpty(recoveryStatus.getText())) {
                    recoveryStatus.setText("");
                    recoveryStatus.setVisibility(View.INVISIBLE);
                }*/
            }
        };
        email.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "executing onClick");
        switch (view.getId()) {
            case R.id.password_recovery_button :
                passwordRecovery.setEnabled(false);
                recoverPassword();
                break;

        }
    }

    private void recoverPassword() {
        /** Hide soft keyboard. */
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        /** Getting email and trim. */
        String emailStr = email.getText().toString().trim();

        /** Validation. */
        if (TextUtils.isEmpty(emailStr)) {
            addErrorMsg(getResources().getString(R.string.err_str_email_empty));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(getResources().getString(R.string.err_str_enter_valid_email));
            }
        }

        /** Error string is for keeping the error that needs to be shown to the user. */
        if (errors.length() > 0) {
            showToast(errors.toString(), SuperToast.Duration.MEDIUM, SuperToast.Background.RED);
            errors.delete(0, errors.length());
        } else {
            sendRecoveryEmail(emailStr);
        }
    }


    private void sendRecoveryEmail(String email) {
        Log.d(TAG, "recovery email invoked");
        showLoader(getResources().getString(R.string.password_recovery_msg));
        passwordRecovery.setEnabled(false);

        JSONObject postData = new JSONObject();
        try {
            postData.put(API.key.PASSWORD_RECOVERY_EMAIL, email.trim());
        } catch (JSONException e) {
            Log.e(TAG, "Exception while adding postdata: " + e.getMessage(), e);
        }

        ExternalCallWithOkHttp.doPost(PasswordRecoveryActivity1.this, postData, API.PASSWORD_RECOVER_API, IncludeAuthentication.NO, new ResponseHandler() {

            @Override
            public void onSuccess(Headers headers, String body) {
                Log.d(TAG, "executing sendRecoveryEmail: onSuccess");
                Message msg = new Message();
                msg.what = PASSWORD_RECOVERY_SUCCESS;
                msg.obj = getResources().getText(R.string.password_recovery_message);
                updateHandler.sendMessage(msg);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing sendRecoveryEmail: onError: " + error);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        passwordRecovery.setEnabled(true);
                    }
                });

                Message msg = new Message();
                msg.what = PASSWORD_RECOVERY_ERROR;
                msg.obj = JsonParseUtils.parseError(error);
                updateHandler.sendMessage(msg);
            }

            @Override
            public void onException(Exception exception) {
                Log.e(TAG, "executing sendRecoveryEmail: onException: " + exception.getMessage(), exception);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        passwordRecovery.setEnabled(true);
                    }
                });

                Message msg = new Message();
                msg.what = PASSWORD_RECOVERY_EXCEPTION;
                msg.obj = exception.getLocalizedMessage();
                updateHandler.sendMessage(msg);
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

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PASSWORD_RECOVERY_SUCCESS:
                    passwordChanged((String) msg.obj);
                    break;
                case PASSWORD_RECOVERY_ERROR:
                   onPasswordRecoveryError((ErrorModel) msg.obj);
                    break;
                case PASSWORD_RECOVERY_EXCEPTION:
                    showToast((String) msg.obj, SuperToast.Duration.EXTRA_LONG, SuperToast.Background.RED);
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + msg.what);
            }
            return true;
        }
    });
    private void showToast(String msg, int length, int color) {
        hideLoader();

        SuperToast superToast = new SuperToast(PasswordRecoveryActivity1.this);
        superToast.setText(msg);
        superToast.setDuration(length);
        superToast.setBackground(color);
        superToast.setTextColor(Color.WHITE);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setGravity(Gravity.TOP, 0, 20);
        superToast.show();
    }

    private void passwordChanged(String message) {
        hideLoader();
        TextView recoveryStatus = (TextView) findViewById(R.id.password_recovery_info);
        recoveryStatus.setText(message);
        recoveryStatus.setVisibility(View.VISIBLE);

        TextView recoveryButton = (TextView) findViewById(R.id.password_recovery_button);
        recoveryButton.setEnabled(false);
    }


    private void onPasswordRecoveryError(ErrorModel errorModel) {
        if (errorModel.getSystemErrorCode() != 0 && errorModel.getErrorCode() == MobileSystemErrorCodeEnum.USER_SOCIAL) {
            showToast(
                    getResources().getString(R.string.password_recovery_social_failed),
                    SuperToast.Duration.EXTRA_LONG,
                    SuperToast.Background.RED);
        } else {
            showToast(
                    errorModel.getReason(),
                    SuperToast.Duration.EXTRA_LONG,
                    SuperToast.Background.RED);
        }

        if (!TextUtils.isEmpty(email.getText())) {
            TextView recoveryButton = (TextView) findViewById(R.id.password_recovery_button);
            recoveryButton.setEnabled(true);
        }
    }
}
