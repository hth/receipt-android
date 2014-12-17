package com.receiptofi.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.utils.Constants;
import com.receiptofi.android.utils.StringUtil;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by PT on 12/11/14.
 */
public class PasswordRecoveryActivity extends Activity implements View.OnClickListener {

    private static final String TAG = PasswordRecoveryActivity.class.getSimpleName();

    protected static final int PASSWORD_RECOVERY_SUCCESS = 0x2565;
    protected static final int PASSWORD_RECOVERY_FAILURE = 0x2566;

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private String emailStr;

    private ProgressDialog loader;

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case PASSWORD_RECOVERY_SUCCESS:
                    passwordChanged(true);
                    break;
                case PASSWORD_RECOVERY_FAILURE:
                    passwordChanged(false);
                    break;
            }
        }
    };

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

                passwordRecovery.setEnabled(editable.length() >= Constants.EMAIL_MIN
                );
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
        // getting username and password
        emailStr = email.getText().toString();

        if (StringUtil.isEmpty(emailStr)) {
            errors.append(this.getResources().getString(R.string.err_str_enter_email));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(this.getResources().getString(R.string.err_str_enter_valid_email));
            }
        }
        // error string is for keeping the error that needs to be shown to the
        // user.
        if (errors.length() > 0) {
            Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
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

        if (StringUtil.isEmpty(email)) {
            errors.append(this.getResources().getString(R.string.err_str_bundle_null));
            Toast toast = Toast.makeText(this, errors, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
            errors.delete(0, errors.length());
            return;
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put(API.key.PASSWORD_RECOVERY_EMAIL, email);
        } catch (JSONException e) {
            Log.d(TAG, "Exception while adding postdata: " + e.getMessage());
        }

        HTTPUtils.doPost(postData, API.PASSWORD_RECOVER_API, new ResponseHandler() {

            @Override
            public void onSuccess(Header[] headers) {
                Log.d(TAG, "executing sendRecoveryInfo: onSuccess");
                myHandler.sendEmptyMessage(PASSWORD_RECOVERY_SUCCESS);
            }

            @Override
            public void onError(int statusCode, String error) {
                Log.d(TAG, "executing sendRecoveryInfo: onError" + error);
                myHandler.sendEmptyMessage(PASSWORD_RECOVERY_SUCCESS);
            }

            @Override
            public void onException(Exception exception) {
                Log.d(TAG, "executing sendRecoveryInfo: onException" + exception.getMessage());
                myHandler.sendEmptyMessage(PASSWORD_RECOVERY_FAILURE);
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
}