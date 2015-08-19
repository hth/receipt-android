package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ExpenseTagModel;

import java.util.List;

/**
 * User: PT
 * Date: 1/18/15 11:11 PM
 */
public class ExpenseTagListAdapter extends ArrayAdapter<ExpenseTagModel> {

    private static final String TAG = ExpenseTagListAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private Context context;
    private List<ExpenseTagModel> tagList;
    private String currTag;

    public ExpenseTagListAdapter(Context context, List<ExpenseTagModel> tags, String currTag) {
        super(context, R.layout.expense_tag_list_item, tags);
        this.context = context;
        this.tagList = tags;
        this.currTag = currTag;
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
                convertView = inflater.inflate(R.layout.expense_tag_list_item, parent, false);

                holder = new ViewHolder();
                holder.tagName = (TextView) convertView.findViewById(R.id.receipt_action_tag_name);
                holder.tagColor = convertView.findViewById(R.id.receipt_action_tag_color);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ExpenseTagModel tagModel = getItem(position);
            holder.tagName.setText(tagModel.getName());
            holder.tagName.setTextColor(context.getResources().getColor(R.color.black));
            holder.tagColor.setBackgroundColor(Color.parseColor(tagModel.getColor()));
            if (!TextUtils.isEmpty(currTag) && currTag.equals(tagModel.getId())) {
                ((ListView) parent).setItemChecked(position, true);
            }
            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private class ViewHolder {
        TextView tagName;
        View tagColor;
    }
}
