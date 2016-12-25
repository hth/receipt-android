package com.receiptofi.receiptapp.views.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.fragments.ExpenseTagFragment;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.model.ExpenseTagModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.service.DeviceService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants.DialogMode;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.db.ExpenseTagUtils;
import com.receiptofi.receiptapp.views.ColorPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Headers;

/**
 * User: PT
 * Date: 4/9/15 1:53 AM
 */
public class ExpenseTagDialog extends DialogFragment {
    private static final String TAG = ExpenseTagDialog.class.getSimpleName();

    private static final String BUNDLE_EXTRA_TAG_ID = "tag_id";
    private String tagId;
    private DialogMode dialogMode = null;
    private ExpenseTagModel tagModel;
    private EditText label;
    private Drawable tagIcon;

    /**
     * Create a new instance of EditTagDialog, providing "id" of
     * tag as arguments.
     */
    public static ExpenseTagDialog newInstance(String tagId) {
        ExpenseTagDialog f = new ExpenseTagDialog();

        // Supply tagId as an argument.
        Bundle args = new Bundle();
        args.putString(BUNDLE_EXTRA_TAG_ID, tagId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String selectedTagId = getArguments().getString(BUNDLE_EXTRA_TAG_ID);
        if (selectedTagId == null) {
            dialogMode = DialogMode.MODE_CREATE;
        } else {
            dialogMode = DialogMode.MODE_UPDATE;
            this.tagId = selectedTagId;
        }

        tagIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_tag)
                .colorRes(R.color.app_theme_bg)
                .actionBarSize();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);
        final ColorPickerView colorPicker = (ColorPickerView) rootView.findViewById(R.id.edit_tag_colorPicker);
        label = (EditText) rootView.findViewById(R.id.edit_tag_label);
        label.setSelected(false);

        if (DialogMode.MODE_UPDATE == dialogMode) {
            tagModel = ExpenseTagUtils.getExpenseTagModels().get(tagId);
            label.setText(tagModel.getTag());
            colorPicker.setColor(Color.parseColor(tagModel.getColor()));
            ((IconDrawable) tagIcon).color(Color.parseColor(tagModel.getColor()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (DialogMode.MODE_UPDATE == dialogMode) {
            builder.setTitle(getString(R.string.expense_tag_dialog_edit_label));
        } else {
            builder.setTitle(getString(R.string.expense_tag_dialog_add_text));
        }
        builder.setIcon(tagIcon);
        builder.setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );

        String positiveButtonText;
        if (DialogMode.MODE_UPDATE == dialogMode) {
            positiveButtonText = getString(R.string.expense_tag_dialog_button_update);
        } else {
            positiveButtonText = getString(R.string.expense_tag_dialog_button_add);
        }

        builder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String tagName = label.getText().toString();
                        String tagColor = String.format("#%06X", (0xFFFFFF & colorPicker.getColor()));
                        JSONObject postData = new JSONObject();

                        switch (dialogMode) {
                            case MODE_CREATE:
                                //TODO remove constant and replace with icon name
                                createExpenseTag(tagName, tagColor, "V101", postData);
                                break;
                            case MODE_UPDATE:
                                //TODO remove constant and replace with icon name
                                updateExpenseTag(tagName, tagColor, "V101", postData);
                                break;
                            default:
                                Log.e(TAG, "Reached unsupported condition, expense tag dialog=" + dialogMode);
                                throw new RuntimeException("Reached unreachable condition");
                        }
                    }
                }
        );

        builder.setView(rootView);
        return builder.create();
    }

    private void createExpenseTag(String tagName, String tagColor, String tagIcon, JSONObject postData) {
        try {
            postData.put("tagName", tagName);
            postData.put("tagColor", tagColor);
            postData.put("tagIcon", tagIcon);

            ExternalCallWithOkHttp.doPost(getActivity(), postData, API.ADD_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                @Override
                public void onSuccess(Headers headers, String body) {
                    DeviceService.onSuccess(headers, body);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.d(TAG, "executing ADD_EXPENSE_TAG: onError: " + error);
                    if (null != AppUtils.getHomePageContext()) {
                        ExpenseTagFragment.showMessage(JsonParseUtils.parseForErrorReason(error), SuperToast.Background.RED, (Activity) AppUtils.getHomePageContext());
                    }
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing ADD_EXPENSE_TAG: onException: " + exception.getMessage());
                    if (null != AppUtils.getHomePageContext()) {
                        ExpenseTagFragment.showMessage(exception.getMessage(), SuperToast.Background.RED, (Activity) AppUtils.getHomePageContext());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Exception while creating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
        }
    }

    private void updateExpenseTag(String tagName, String tagColor, String tagIcon, JSONObject postData) {
        Log.d(TAG, "After dialog dismiss: " + tagColor);
        if (!(tagModel.getTag().equals(tagName)) || !(tagModel.getColor().equals(tagColor))) {
            try {
                postData.put("tagId", tagModel.getId());
                postData.put("tagName", tagName);
                postData.put("tagColor", tagColor);
                postData.put("tagIcon", tagIcon);

                /**
                 * Update DB before sending to server. being pro-active about it. Will update
                 * once again after receiving response from server.
                 */
                ExpenseTagUtils.updateExpenseTag(tagModel.getId(), tagName, tagColor, tagIcon);

                ExternalCallWithOkHttp.doPost(getActivity(), postData, API.UPDATE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                    @Override
                    public void onSuccess(Headers headers, String body) {
                        DeviceService.onSuccess(headers, body);
                    }

                    @Override
                    public void onError(int statusCode, String error) {
                        Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onError: " + error);
                        ExpenseTagFragment.showMessage(JsonParseUtils.parseForErrorReason(error), SuperToast.Background.RED, (Activity) AppUtils.getHomePageContext());
                    }

                    @Override
                    public void onException(Exception exception) {
                        Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onException: " + exception.getMessage());
                        ExpenseTagFragment.showMessage(exception.getMessage(), SuperToast.Background.RED, (Activity) AppUtils.getHomePageContext());
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, "Exception while updating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart Opened Expense Tag dialog");
        final Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        if (DialogMode.MODE_CREATE == dialogMode) {
            positiveButton.setEnabled(false);
        }
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                positiveButton.setEnabled(!TextUtils.isEmpty(charSequence.toString()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                positiveButton.setEnabled(!TextUtils.isEmpty(editable.toString()));
            }
        };
        label.addTextChangedListener(textWatcher);
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final MainMaterialDrawerActivity activity = (MainMaterialDrawerActivity) getActivity();
        if (activity.expenseTagFragment != null) {
            (activity.expenseTagFragment).onDismiss(dialog);
        }
    }
}
