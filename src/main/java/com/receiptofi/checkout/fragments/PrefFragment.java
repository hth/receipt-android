package com.receiptofi.checkout.fragments;

import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.dialog.EditTagDialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by PT on 4/9/15.
 */
public class PrefFragment extends Fragment {

    private String TAG = "PrefFragment";

    private List<ExpenseTagModel> tagModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pref_fragment, container, false);

        ListView tagListView = (ListView)rootView.findViewById(R.id.pref_fragment_expense_tag_list);
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        tagListView.setAdapter(new ExpenseTagListAdapter(getActivity(), tagModelList));

        return rootView;
    }

    View.OnClickListener onEditButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View button) {
            Object tag = button.getTag();
            try {
                int position = (Integer)tag;
                ExpenseTagModel tagModel = tagModelList.get(position);
                Log.d(TAG, "Selected tag label is: " + tagModel.getName());

                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment editTagDialog = EditTagDialog.newInstance(tagModel.getId());
                editTagDialog.show(ft, "dialog");
               // int num = newFragment.show(ft, "dialog");

            }
            catch (ClassCastException e) {
            }
        }
    };

    View.OnClickListener onDeleteButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View button) {
            Object tag = button.getTag();
            try {
                int position = (Integer)tag;
                final ExpenseTagModel tagModel = tagModelList.get(position);
                Log.d(TAG, "Selected tag name is: " + tagModel.getName());

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.expense_tag_dialog_delete_label))
                        .setMessage(getString(R.string.expense_tag_dialog_text, tagModel.getName()))
                        .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setPositiveButton(getString(R.string.expense_tag_dialog_button_delete), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: call api to delete
                                tagModel.getId();
                                tagModel.getName();
                                tagModel.getColor();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            catch (ClassCastException e) {
            }
        }
    };

    public class ExpenseTagListAdapter extends ArrayAdapter<ExpenseTagModel> {

        private final String TAG = ExpenseTagListAdapter.class.getSimpleName();
        private final LayoutInflater inflater;
        private List<ExpenseTagModel> tagList;

        public ExpenseTagListAdapter(Context context, List<ExpenseTagModel> tags) {
            super(context, R.layout.pref_fragment_exp_tag_list_item, tags);
            this.tagList = tags;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return tagList.size();
        }

        @Override
        public ExpenseTagModel getItem(int position) {
            return tagList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                ViewHolder holder;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.pref_fragment_exp_tag_list_item, parent, false);

                    holder = new ViewHolder();
                    holder.tagName = (TextView) convertView.findViewById(R.id.exp_list_tag_name);
                    holder.tagColor = convertView.findViewById(R.id.exp_list_tag_color);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                ImageButton editButton = (ImageButton)convertView.findViewById(R.id.exp_list_tag_edit);
                editButton.setTag(position);
                editButton.setOnClickListener(onEditButtonClicked);
                ImageButton deleteButton = (ImageButton)convertView.findViewById(R.id.exp_list_tag_delete);
                deleteButton.setTag(position);
                deleteButton.setOnClickListener(onDeleteButtonClicked);

                ExpenseTagModel tagModel = getItem(position);
                holder.tagName.setText(tagModel.getName());
                holder.tagColor.setBackgroundColor(Color.parseColor(tagModel.getColor()));

                return convertView;
            } catch (Exception e) {
                Log.d(TAG, "Exception " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private class ViewHolder {
            TextView tagName;
            View tagColor;
        }
    }
}