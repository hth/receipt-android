package com.receiptofi.checkout.views.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.Constants.DialogMode;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;
import com.receiptofi.checkout.views.ToastBox;
import com.squareup.okhttp.Headers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PT on 4/9/15.
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
                                            if (null != getActivity()) {
                                                final String errorMessage = error;
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastBox.makeText(getActivity(), JsonParseUtils.parseError(errorMessage), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onException(Exception exception) {
                                            Log.d(TAG, "executing ADD_EXPENSE_TAG: onException: " + exception.getMessage());
                                            if (null != getActivity()) {
                                                final String errorMessage = exception.getMessage();
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ToastBox.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                } catch (JSONException e) {
                                    Log.e(TAG, "Exception while creating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                }
                                break;
                            case MODE_UPDATE:
                                Log.d(TAG, "After dialog dismiss: " + tagColor);
                                if (!(tagModel.getName().equals(tagName)) || !(tagModel.getColor().equals(tagColor))) {
                                    try {
                                        postData.put("tagId", tagModel.getId());
                                        postData.put("tagName", tagName);
                                        postData.put("tagColor", tagColor);

                                        ExternalCallWithOkHttp.doPost(getActivity(), postData, API.UPDATE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                                            @Override
                                            public void onSuccess(Headers headers, String body) {
                                                DeviceService.onSuccess(headers, body);
                                            }

                                            @Override
                                            public void onError(int statusCode, String error) {
                                                Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onError: " + error);
                                                ToastBox.makeText(getActivity(), JsonParseUtils.parseError(error), Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onException(Exception exception) {
                                                Log.d(TAG, "executing UPDATE_EXPENSE_TAG: onException: " + exception.getMessage());
                                                ToastBox.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception while updating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                    }
                                }
                                break;
                            default:
                                throw new RuntimeException("Reached unreachable condition");
                        }
                    }
                }
        );

        builder.setView(rootView);
        return builder.create();
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
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
