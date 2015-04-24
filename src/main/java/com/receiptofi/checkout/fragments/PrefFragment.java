package com.receiptofi.checkout.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.dialog.ExpenseTagDialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by PT on 4/9/15.
 */
public class PrefFragment extends Fragment {
    private static final String TAG = PrefFragment.class.getSimpleName();

    private List<ExpenseTagModel> tagModelList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pref_fragment, container, false);

        Button addButton = (Button)rootView.findViewById(R.id.pref_fragment_add_expense_tag);
        addButton.setOnClickListener(onAddButtonClicked);

        ListView tagListView = (ListView)rootView.findViewById(R.id.pref_fragment_expense_tag_list);
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        tagListView.setAdapter(new ExpenseTagListAdapter(getActivity(), tagModelList));

        return rootView;
    }

    View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View button) {
            try {
                Log.d(TAG, "Add new tag");

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
                DialogFragment editTagDialog = ExpenseTagDialog.newInstance(null);
                editTagDialog.show(ft, "dialog");
                // int num = newFragment.show(ft, "dialog");

            }
            catch (ClassCastException e) {
            }
        }
    };

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
                DialogFragment editTagDialog = ExpenseTagDialog.newInstance(tagModel.getId());
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
                                String tagId = tagModel.getId();
                                String tagName = tagModel.getName();

                                if (null != tagId || null != tagName) {
                                    JSONObject postData = new JSONObject();
                                    try {
                                        postData.put("tagId", tagId);
                                        postData.put("tagName", tagName);

                                        ExternalCall.doPost(postData, API.DELETE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
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
                                        Log.e(TAG, "Exception while deleting expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                    }
                                }
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } catch (ClassCastException e) {
                Log.e(TAG, "Error while deleting expense tag reason=" + e.getLocalizedMessage(), e);
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