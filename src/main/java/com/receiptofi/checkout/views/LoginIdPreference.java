package com.receiptofi.checkout.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.utils.UserUtils;

/**
 * User: PT
 * Date: 12/26/14 11:43 AM
 */
public class LoginIdPreference extends EditTextPreference {

    public LoginIdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(UserUtils.isValidEmail(editable.toString()));
            }
        };

        EditText text = (EditText) dialog.findViewById(android.R.id.edit);
        text.setText("");
        text.setHint(R.string.hint_email);
        text.addTextChangedListener(textWatcher);
    }
}
