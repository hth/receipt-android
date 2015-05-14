package com.receiptofi.checkout.views.dialog;

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

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.Constants.DialogMode;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;

import org.apache.http.Header;
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
                        int colorCode = colorPicker.getColor();
                        String tagColor = String.format("#%06X", (0xFFFFFF & colorCode));

                        switch (dialogMode) {
                            case MODE_CREATE:
                                if (null != tagName || null != tagColor) {
                                    JSONObject postData = new JSONObject();
                                    try {
                                        postData.put("tagId", tagId);
                                        postData.put("tagName", tagName);
                                        postData.put("tagColor", tagColor);

                                        ExternalCall.doPost(postData, API.ADD_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                                            @Override
                                            public void onSuccess(Header[] headers, String body) {
                                                DeviceService.onSuccess(headers, body);
                                            }

                                            @Override
                                            public void onError(int statusCode, String error) {

                                            }

                                            @Override
                                            public void onException(Exception exception) {

                                            }
                                        });

                                    } catch (JSONException e) {
                                        Log.e(TAG, "Exception while creating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                    }
                                }
                                break;
                            case MODE_UPDATE:
                                Log.d("After dialog dismiss: ", tagColor);
                                if (!(tagModel.getName().equals(tagName)) || !(tagModel.getColor().equals(tagColor))) {
                                    String tagId = tagModel.getId();

                                    if (null != tagId || null != tagName || null != tagColor) {
                                        JSONObject postData = new JSONObject();
                                        try {
                                            postData.put("tagId", tagId);
                                            postData.put("tagName", tagName);
                                            postData.put("tagColor", tagColor);

                                            ExternalCall.doPost(postData, API.UPDATE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                                                @Override
                                                public void onSuccess(Header[] headers, String body) {
                                                    DeviceService.onSuccess(headers, body);
                                                }

                                                @Override
                                                public void onError(int statusCode, String error) {

                                                }

                                                @Override
                                                public void onException(Exception exception) {

                                                }
                                            });

                                        } catch (JSONException e) {
                                            Log.e(TAG, "Exception while updating expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                        }
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
}
