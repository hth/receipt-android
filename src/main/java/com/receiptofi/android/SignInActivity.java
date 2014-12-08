package com.receiptofi.android;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.android.http.API;
import com.receiptofi.android.utils.StringUtil;
import com.receiptofi.android.utils.UserUtils;


/**
 * Created by PT on 12/7/14.
 */
public class SignInActivity extends ParentActivity implements View.OnClickListener {

    private static final String TAG = SignInActivity.class.getSimpleName();

    private StringBuilder errors = new StringBuilder();
    private EditText email;
    private EditText password;
    private String emailStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_page);

        final TextView signIn = (TextView)findViewById(R.id.sign_in_button);
        signIn.setOnClickListener(this);

        email = (EditText) findViewById(R.id.email);
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordStr = password.getText().toString();
                signIn.setEnabled(editable.length() > 5 && passwordStr.length() > 3);
            }
        });
        password = (EditText) findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailStr = email.getText().toString();
                signIn.setEnabled(editable.length() > 3 && emailStr.length() > 5);
            }
        });

        TextView forgotPassword = (TextView)findViewById(R.id.forgot_password_button);
        forgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // show Notification page
               // Intent i = new Intent(getApplicationContext(), PasswordRecoveryActivity.class);
                //startActivity(i);
            }
        });

        LinearLayout facebooklogin = (LinearLayout)findViewById(R.id.facebook_login);
        facebooklogin.setOnClickListener(this);

        LinearLayout googlelogin = (LinearLayout)findViewById(R.id.google_login);
        googlelogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "executing onClick");
        switch (view.getId()) {
            case R.id.sign_in_button:
                login();
                break;
            case R.id.google_logo:
                break;
        }
    }

    private void login() {
        // getting username and password
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();

        if (StringUtil.isEmpty(emailStr)) {
            errors.append(this.getResources().getString(R.string.enter_email));
        } else {
            if (!UserUtils.isValidEmail(emailStr)) {
                addErrorMsg(this.getResources().getString(R.string.enter_valid_email));
            }
        }
        if (StringUtil.isEmpty(passwordStr)) {
            addErrorMsg(this.getResources().getString(R.string.enter_password));
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
            bundle.putString(API.key.EMAIL, emailStr);
            bundle.putString(API.key.PASSWORD, passwordStr);
            authenticateUser(false, bundle);
        }

    }

    private void addErrorMsg(String msg) {
        if (errors.length() == 0) {
            errors.append(msg);
        } else {
            errors.append("\n").append("\n").append(msg);
        }
    }
}