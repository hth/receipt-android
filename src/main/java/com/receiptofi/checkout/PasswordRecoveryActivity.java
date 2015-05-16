package com.receiptofi.checkout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.utils.UserUtils;
import com.receiptofi.checkout.utils.Validation;
import com.receiptofi.checkout.views.ToastBox;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by PT on 12/11/14.
 */
public class PasswordRecoveryActivity extends Activity implements View.OnClickListener {

    protected static final int PASSWORD_RECOVERY_SUCCESS = 0x2565;
    protected static final int PASSWORD_RECOVERY_FAILURE = 0x2566;
    private static final String TAG = PasswordRecoveryActivity.class.getSimpleName();
    private StringBuilder errors = new StringBuilder();
    private EditText email;

    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_recovery);

        final TextView passwordRecovery = (TextView) findViewById(R.id.password_recovery_button);
        passwordRecovery.setOnClickListener(this);

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
            }
        };

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(textWatcher);

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "executing onClick");
        switch (view.getId()) {
            case R.id.password_recovery_button:
                Log.d(TAG, "password_recovery_button clicked");
                email.setEnabled(false);
                final TextView passwordRecovery = (TextView) findViewById(R.id.password_recovery_button);
                passwordRecovery.setEnabled(false);
                recoverPassword();
                break;
            default:
                Log.d(TAG, "done executing onClick no id match");
                break;
        }
    }

    private void recoverPassword() {
        // Hide soft keyboard
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        // getting username and password
        String emailStr = email.getText().toString();

        if (TextUtils.isEmpty(emailStr)) {
            errors.append(this.getResources().getString(R.string.err_str_enter_email));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(this.getResources().getString(R.string.err_str_enter_valid_email));
            }
        }
        // error string is for keeping the error that needs to be shown to the
        // user.
        if (errors.length() > 0) {
            Toast toast = ToastBox.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
        } else {
            sendRecoveryInfo(emailStr);
        }
    }

    private void sendRecoveryInfo(String email) {
        Log.d(TAG, "executing authenticateSignUp");
        showLoader(this.getResources().getString(R.string.login_auth_msg));

        if (TextUtils.isEmpty(email)) {
            errors.append(this.getResources().getString(R.string.err_str_bundle_null));
            Toast toast = ToastBox.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put(API.key.PASSWORD_RECOVERY_EMAIL, email);
        } catch (JSONException e) {
            Log.e(TAG, "Exception while adding postdata: " + e.getMessage(), e);
        }

        ExternalCall.doPost(PasswordRecoveryActivity.this, postData, API.PASSWORD_RECOVER_API, IncludeAuthentication.NO, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers, String body) {
                Log.d(TAG, "executing sendRecoveryInfo: onSuccess");
                updateHandler.sendEmptyMessage(PASSWORD_RECOVERY_SUCCESS);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing sendRecoveryInfo: onError" + error);
                updateHandler.sendEmptyMessage(PASSWORD_RECOVERY_SUCCESS);
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing sendRecoveryInfo: onException" + exception.getMessage());
                updateHandler.sendEmptyMessage(PASSWORD_RECOVERY_FAILURE);
            }
        });
        //TODO
        // startActivity(new Intent(PasswordRecoveryActivity.this, LaunchActivity.class));
        // finish();
    }

    private void addErrorMsg(String msg) {
        if (errors.length() == 0) {
            errors.append(msg);
        } else {
            errors.append("\n").append("\n").append(msg);
        }
    }

    public void showLoader(String msg) {
        loader = new ProgressDialog(this);
        loader.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.setIndeterminate(true);
        loader.setMessage(msg);
        loader.show();
    }

    public void hideLoader() {
        if (loader != null) {
            loader.dismiss();
        }
        loader = null;
    }

    private void passwordChanged(boolean success) {
        hideLoader();
        TextView recoveryStatus = (TextView) findViewById(R.id.password_recovery_info);
        if (success) {
            recoveryStatus.setText(PasswordRecoveryActivity.this.getText(R.string.password_recovery_message));
        } else {
            recoveryStatus.setText(PasswordRecoveryActivity.this.getText(R.string.password_recovery_failed));
            final TextView passwordRecovery = (TextView) findViewById(R.id.password_recovery_button);
            passwordRecovery.setEnabled(true);
        }
        recoveryStatus.setVisibility(View.VISIBLE);
    }

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PASSWORD_RECOVERY_SUCCESS:
                    passwordChanged(true);
                    break;
                case PASSWORD_RECOVERY_FAILURE:
                    passwordChanged(false);
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + msg);
            }
            return true;
        }
    });
}
