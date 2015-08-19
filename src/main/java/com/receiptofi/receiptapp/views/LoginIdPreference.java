package com.receiptofi.receiptapp.views;

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
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;

/**
 * User: PT
 * Date: 12/26/14 11:43 AM
 */
public class LoginIdPreference extends EditTextPreference {
    private Drawable icon;
    private boolean isSocialAccount;

    public LoginIdPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        icon = new IconDrawable(getContext(), Iconify.IconValue.fa_envelope)
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
        dialog.setTitle(R.string.pref_login_change_title);
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
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(UserUtils.isValidEmail(editable.toString()));
                }
            };
            text.setText("");
            text.setHint(R.string.hint_email);
            text.addTextChangedListener(textWatcher);
            text.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);
        } else {
            final EditText text = (EditText) dialog.findViewById(android.R.id.edit);
            text.setVisibility(View.INVISIBLE);

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextAppearance(getContext(), R.style.alert_dialog_text_appearance_medium);
            textView.setText(getContext().getString(R.string.pref_login_social_message));
            textView.setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }
}
