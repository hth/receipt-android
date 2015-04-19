package com.receiptofi.checkout.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by PT on 4/9/15.
 */
public class EditTagDialog extends DialogFragment {
    private static final String TAG = EditTagDialog.class.getSimpleName();

    private static final String BUNDLE_EXTRA_TAG_ID = "tag_id";
    private String tagId;

    /**
     * Create a new instance of EditTagDialog, providing "id" of
     * tag as arguments.
     */
    public static EditTagDialog newInstance(String tagId) {
        EditTagDialog f = new EditTagDialog();

        // Supply tagId as an argument.
        Bundle args = new Bundle();
        args.putString(BUNDLE_EXTRA_TAG_ID, tagId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tagId = getArguments().getString(BUNDLE_EXTRA_TAG_ID);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ExpenseTagModel tagModel = ExpenseTagUtils.getExpenseTagModels().get(tagId);

        Log.d("Color at start: ", tagModel.getColor());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);
        final EditText label = (EditText) rootView.findViewById(R.id.edit_tag_label);
        label.setText(tagModel.getName());
        label.setSelected(false);
        final ColorPickerView colorPicker = (ColorPickerView) rootView.findViewById(R.id.edit_tag_colorPicker);
        colorPicker.setColor(Color.parseColor(tagModel.getColor()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.expense_tag_dialog_edit_label));
        builder.setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        );

        builder.setPositiveButton(getString(R.string.expense_tag_dialog_button_update),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String labelStr = label.getText().toString();
                        int colorCode = colorPicker.getColor();
                        String hexColor = String.format("#%06X", (0xFFFFFF & colorCode));
                        Log.d("After dialog dismiss:", hexColor);
                        if (!(tagModel.getName().equals(labelStr)) || !(tagModel.getColor().equals(hexColor))) {
                            String tagId = tagModel.getId();
                            String tagName = labelStr;
                            String tagColor = hexColor;

                            if (null != tagId || null != tagName || null != tagColor) {
                                JSONObject postData = new JSONObject();
                                try {
                                    postData.put("tagId", tagId);
                                    postData.put("tagName", tagName);
                                    postData.put("tagColor", tagColor);

                                    ExternalCall.doPost(postData, API.UPDATDE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
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
                    }
                }
        );

        builder.setView(rootView);
        return builder.create();
    }
}
