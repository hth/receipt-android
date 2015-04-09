package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.fragments.ReceiptListFragment;
import com.receiptofi.checkout.model.ReceiptGroupHeader;
import com.receiptofi.checkout.model.ReceiptModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ReceiptListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = ReceiptListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private Context context;

    public ReceiptListAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return ReceiptListFragment.groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ReceiptListFragment.children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ReceiptListFragment.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ReceiptListFragment.children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        try {
            ChildViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.receipt_list_child, parent, false);
                holder = new ChildViewHolder();

                holder.bizName = (TextView) convertView.findViewById(R.id.exp_list_child_buz_name);
                holder.date = (TextView) convertView.findViewById(R.id.exp_list_child_date);
                holder.expenseTag = convertView.findViewById(R.id.exp_list_child_tag_color);
                holder.amount = (TextView) convertView.findViewById(R.id.exp_list_child_amount);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            ReceiptModel receiptData = (ReceiptModel) getChild(groupPosition, childPosition);
            DateFormat inputDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            DateFormat outputDF = new SimpleDateFormat("MMM dd',' yyyy HH:mm a");
            String formattedDate = outputDF.format(inputDF.parse(receiptData.getReceiptDate()));

            holder.bizName.setText(receiptData.getBizName());
            holder.date.setText(formattedDate);
            holder.amount.setText(context.getString(R.string.receipt_list_child_amount, receiptData.getTotal()));
            // TODO: complete this later
            /*
            if(){
                holder.tagColor.setBackgroundColor(Color.parseColor(tagModel.getColor()));
            }
            */

            return convertView;
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "IndexOutOfBoundsException " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            Log.d(TAG, "ParseException " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder holder;

        try {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.receipt_list_parent, parent, false);

                holder = new ParentViewHolder();
                holder.month = (TextView) convertView.findViewById(R.id.exp_list_header_month);
                holder.amount = (TextView) convertView.findViewById(R.id.exp_list_header_amount);
                convertView.setTag(holder);
            } else {
                holder = (ParentViewHolder) convertView.getTag();
            }

            ReceiptGroupHeader headerData = (ReceiptGroupHeader) getGroup(groupPosition);
            String year = headerData.getYear();
            String month = headerData.getMonth();
            DateFormat inputDF = new SimpleDateFormat("M yyyy");
            DateFormat outputDF = new SimpleDateFormat("MMM yyyy");
            String formattedMonth = outputDF.format(inputDF.parse(month + " " + year));

            holder.month.setText(context.getString(R.string.receipt_list_header_month, formattedMonth, headerData.getCount()));
            holder.amount.setText(context.getString(R.string.receipt_list_header_amount, headerData.getTotal()));
            return convertView;

        } catch (ParseException e) {
            Log.d(TAG, "ParseException " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ParentViewHolder {
        TextView month;
        TextView amount;
    }

    private class ChildViewHolder {
        TextView bizName;
        TextView date;
        View expenseTag;
        TextView amount;
    }
}