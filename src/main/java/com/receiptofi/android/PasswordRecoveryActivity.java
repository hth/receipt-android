package com.receiptofi.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.android.utils.StringUtil;
import com.receiptofi.android.utils.UserUtils;

/**
 * Created by PT on 12/11/14.
 */
public class PasswordRecoveryActivity extends Activity implements View.OnClickListener {

    private static final String TAG = PasswordRecoveryActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private String emailStr;

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

                passwordRecovery.setEnabled(editable.length() > 5);
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
        TextView infoSent = (TextView) findViewById(R.id.password_recovery_info);
        infoSent.setVisibility(View.VISIBLE);

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
}
