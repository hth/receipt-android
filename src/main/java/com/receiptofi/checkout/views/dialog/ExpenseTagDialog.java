package com.receiptofi.checkout.views.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.MainMaterialDrawerActivity;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.Constants.DialogMode;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;
import com.receiptofi.checkout.views.ToastBox;
import com.squareup.okhttp.Headers;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

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
            label.setText(tagModel.getName());
            colorPicker.setColor(Color.parseColor(tagModel.getColor()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.expense_tag_dialog_edit_label));
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
                                createExpenseTag(tagName, tagColor, postData);
                                break;
                            case MODE_UPDATE:
                                updateExpenseTag(tagName, tagColor, postData);
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

    private void createExpenseTag(String tagName, String tagColor, JSONObject postData) {
        try {
            postData.put("tagName", tagName);
            postData.put("tagColor", tagColor);

            ExternalCallWithOkHttp.doPost(getActivity(), postData, API.ADD_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                @Override
                public void onSuccess(Headers headers, String body) {
                    DeviceService.onSuccess(headers, body);
                }

                @Override
                public void onError(int statusCode, String error) {
                    Log.d(TAG, "executing ADD_EXPENSE_TAG: onError: " + error);
                    if (null != AppUtils.getHomePageContext()) {
                        showMessage(error, (Activity) AppUtils.getHomePageContext());
                    }
                }

                @Override
                public void onException(Exception exception) {
                    Log.d(TAG, "executing ADD_EXPENSE_TAG: onException: " + exception.getMessage());
                    if (null != AppUtils.getHomePageContext()) {
                        showMessage(exception.getMessage(), (Activity) AppUtils.getHomePageContext());
                    }
                }
            });

        } catch (JSONException e) {
            Log.e(TAG, "Exception while creating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
        }
    }

    private void updateExpenseTag(String tagName, String tagColor, JSONObject postData) {
        Log.d(TAG, "After dialog dismiss: " + tagColor);
        if (!(tagModel.getName().equals(tagName)) || !(tagModel.getColor().equals(tagColor))) {
            try {
                postData.put("tagId", tagModel.getId());
                postData.put("tagName", tagName);
                postData.put("tagColor", tagColor);

                /**
                 * Update DB before sending to server. being pro-active about it. Will update
                 * once again after receiving response from server.
                 */
                ExpenseTagUtils.updateExpenseTag(tagModel.getId(), tagName, tagColor);

                ExternalCallWithOkHttp.doPost(getActivity(), postData, API.UPDATE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                    @Override
                    public void onSuccess(Headers headers, String body) {
                        DeviceService.onSuccess(headers, body);
                    }

                    @Override
                    public void onError(int statusCode, String error) {
                        Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onError: " + error);
                        showMessage(error, (Activity) AppUtils.getHomePageContext());
                    }

                    @Override
                    public void onException(Exception exception) {
                        Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onException: " + exception.getMessage());
                        showMessage(exception.getMessage(), (Activity) AppUtils.getHomePageContext());
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

        Log.d("!!!!!!!!!!       ", "onStart");
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

    /**
     * Show Toast message.
     *
     * @param message
     * @param context
     */
    private static void showMessage(final String message, final Activity context) {
        Assert.assertNotNull("Context should not be null", context);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperActivityToast superActivityToast = new SuperActivityToast(context);
                superActivityToast.setText(message);
                superActivityToast.setDuration(SuperToast.Duration.SHORT);
                superActivityToast.setBackground(SuperToast.Background.BLUE);
                superActivityToast.setTextColor(Color.WHITE);
                superActivityToast.setTouchToDismiss(true);
                superActivityToast.show();
            }
        });
    }
}
