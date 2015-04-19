package com.receiptofi.checkout.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.utils.Constants.DialogMode;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ColorPickerView;

/**
 * Created by PT on 4/9/15.
 */
public class ExpenseTagDialog extends DialogFragment {

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
        if(selectedTagId == null){
            dialogMode = DialogMode.MODE_ADD;
        } else {
            dialogMode = DialogMode.MODE_EDIT;
            this.tagId = selectedTagId;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit_tag, null);
        final ColorPickerView colorPicker = (ColorPickerView)rootView.findViewById(R.id.edit_tag_colorPicker);
        label = (EditText)rootView.findViewById(R.id.edit_tag_label);
        label.setSelected(false);
        label.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        if(DialogMode.MODE_EDIT == dialogMode){
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
        String positiveButtonText = null;
        if(DialogMode.MODE_EDIT == dialogMode){
            positiveButtonText = getString(R.string.expense_tag_dialog_button_update);
        } else {
            positiveButtonText = getString(R.string.expense_tag_dialog_button_add);
        }

        builder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String labelStr = label.getText().toString();
                        int colorCode = colorPicker.getColor();
                        String hexColor = String.format("#%06X", (0xFFFFFF & colorCode));
                        if(DialogMode.MODE_EDIT == dialogMode){
                            Log.d("@@@@@@@@@@@ Color after dialog dismiss: ", hexColor);
                            if (!(tagModel.getName().equals(labelStr)) || !(tagModel.getColor().equals(hexColor))){
                                String tagId = tagModel.getId();
                                String tagName = labelStr;
                                String tagcolor = hexColor;
                                // TODO:  call update tag api

                            }
                        } else {
                            String tagName = labelStr;
                            String tagcolor = hexColor;
                            // TODO:  call add tag api

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
        final Button positiveButton = ((AlertDialog)getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        if(DialogMode.MODE_ADD == dialogMode){
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
