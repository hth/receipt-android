package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.fragments.FilterListFragment;
import com.receiptofi.receiptapp.model.ReceiptGroupHeader;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by PT on 3/28/15.
 */
public class FilterListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = FilterListAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private Context context;

    private DateFormat inputDF = new SimpleDateFormat("M yyyy");
    private DateFormat outputDF = new SimpleDateFormat("MMM yyyy");

    public FilterListAdapter(Context context) {
        super();
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getGroupCount() {
        return FilterListFragment.groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return FilterListFragment.children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return FilterListFragment.groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return FilterListFragment.children.get(groupPosition).get(childPosition);
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
                holder.amount = (TextView) convertView.findViewById(R.id.exp_list_child_amount);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            ReceiptModel receiptData = (ReceiptModel) getChild(groupPosition, childPosition);
            holder.bizName.setText(receiptData.getBizName());
            holder.date.setText(Constants.MMM_DD_DF.format(Constants.ISO_DF.parse(receiptData.getReceiptDate())));
            holder.amount.setText(context.getString(R.string.receipt_list_child_amount, AppUtils.currencyFormatter().format(receiptData.getTotal())));

            return convertView;
        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "IndexOutOfBoundsException " + e.getLocalizedMessage(), e);
        } catch (ParseException e) {
            Log.d(TAG, "ParseException " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getLocalizedMessage(), e);
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
            String formattedMonth = outputDF.format(inputDF.parse(headerData.getMonth() + " " + headerData.getYear()));
            holder.month.setText(context.getString(R.string.receipt_list_header_month, formattedMonth, headerData.getCount()));
            if (FilterListFragment.hideTotal) {
                holder.amount.setVisibility(View.GONE);
            } else {
                holder.amount.setVisibility(View.VISIBLE);
                holder.amount.setText(context.getString(R.string.receipt_list_header_amount, AppUtils.currencyFormatter().format(headerData.getTotal())));
            }

            /** Set the group 0 to be always expanded. */
            if (groupPosition == 0) {
                ExpandableListView expandableListView = (ExpandableListView) parent;
                expandableListView.expandGroup(groupPosition);
            }

            return convertView;
        } catch (ParseException e) {
            Log.d(TAG, "ParseException reason=" + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
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
        TextView amount;
    }
}