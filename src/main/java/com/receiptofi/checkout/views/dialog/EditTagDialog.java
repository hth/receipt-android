package com.receiptofi.checkout.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;

/**
 * Created by PT on 4/9/15.
 */
public class EditTagDialog extends DialogFragment {

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

        Log.d("@@@@@@@@@@@ Color at start: ", tagModel.getColor());
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);
        final EditText label = (EditText)rootView.findViewById(R.id.edit_tag_label);
        label.setText(tagModel.getName());
        label.setSelected(false);
        label.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        final ColorPickerView colorPicker = (ColorPickerView)rootView.findViewById(R.id.edit_tag_colorPicker);
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
                        Log.d("@@@@@@@@@@@ Color after dialog dismiss: ", hexColor);
                        if (!(tagModel.getName().equals(labelStr)) || !(tagModel.getColor().equals(hexColor))){
                            String tagId = tagModel.getId();
                            String tagName = labelStr;
                            String tagcolor = hexColor;
                            // TODO:  call update tag api

                        }
                    }
                }
        );

        builder.setView(rootView);
        return builder.create();
    }
}
