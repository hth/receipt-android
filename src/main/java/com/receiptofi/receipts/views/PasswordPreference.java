package com.receiptofi.receipts.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.receipts.R;
import com.receiptofi.receipts.utils.AppUtils;
import com.receiptofi.receipts.utils.Validation;

/**
 * User: PT
 * Date: 12/26/14 11:44 AM
 */
public class PasswordPreference extends EditTextPreference {
    private Drawable icon;
    private boolean isSocialAccount;

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        icon = new IconDrawable(getContext(), Iconify.IconValue.fa_lock)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize();

        isSocialAccount = AppUtils.isSocialAccount();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setIcon(icon);
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.setTitle(R.string.pref_password_change_title);
        View title = dialog.findViewById(getContext().getResources().getIdentifier("alertTitle", "id", "android"));
        ((TextView) title).setTextAppearance(getContext(), R.style.alert_dialog);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        if (!isSocialAccount) {
            final EditText text = (EditText) dialog.findViewById(android.R.id.edit);
            final TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (TextUtils.isEmpty(editable.toString())) {
                        text.setHintTextColor(getContext().getResources().getColor(R.color.gray_dark));
                    }
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(editable.length() >= Validation.PASSWORD_MIN_LENGTH);
                }
            };
            text.setText("");
            text.setHint(R.string.hint_password);
            text.addTextChangedListener(textWatcher);
            text.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);
            textView.setText(getContext().getString(R.string.pref_password_message, Validation.PASSWORD_MIN_LENGTH));
        } else {
            final EditText text = (EditText) dialog.findViewById(android.R.id.edit);
            text.setVisibility(View.INVISIBLE);

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);
            textView.setText(getContext().getString(R.string.pref_password_social_message));
            textView.setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }
}