package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.fragments.ReceiptListFragment;
import com.receiptofi.receiptapp.model.ReceiptGroupHeader;
import com.receiptofi.receiptapp.model.ReceiptModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReceiptListAdapter extends BaseExpandableListAdapter {

    private static final String TAG = ReceiptListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private Context context;

    private DateFormat inputDF = new SimpleDateFormat("M yyyy", Locale.US);
    private DateFormat outputDF = new SimpleDateFormat("MMM yyyy", Locale.US);

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
    public View getChildView(
            int groupPosition,
            final int childPosition,
            boolean isLastChild,
            View convertView,
            ViewGroup parent
    ) {
        try {
            ChildViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.receipt_list_child, parent, false);
                holder = new ChildViewHolder();

                holder.expenseTag = convertView.findViewById(R.id.exp_list_child_tag_color);
                holder.bizName = (TextView) convertView.findViewById(R.id.exp_list_child_buz_name);
                holder.date = (TextView) convertView.findViewById(R.id.exp_list_child_date);
                holder.amount = (TextView) convertView.findViewById(R.id.exp_list_child_amount);
                holder.splitCout = (TextView)convertView.findViewById(R.id.tvSplitCount);
                holder.splitCountImgView = (ImageView)convertView.findViewById(R.id.imvReceiptCount);
                convertView.setTag(holder);
            } else {
                holder = (ChildViewHolder) convertView.getTag();
            }

            ReceiptModel receiptData = (ReceiptModel) getChild(groupPosition, childPosition);

            /** Two checks. Check expenseTagModel is not null for avoiding to fail when expenseTagId is not empty. */
            if (!TextUtils.isEmpty(receiptData.getExpenseTagId()) && null != receiptData.getExpenseTagModel()) {
                String colorCode = receiptData.getExpenseTagModel().getColor();
                holder.expenseTag.setBackgroundColor(Color.parseColor(colorCode));
            } else {
                holder.expenseTag.setBackgroundColor(Color.TRANSPARENT);
            }
            holder.bizName.setText(receiptData.getBizName());
            holder.date.setText(Constants.MMM_DD_DF.format(Constants.ISO_DF.parse(receiptData.getReceiptDate())));
            holder.amount.setText(
                    context.getString(
                            R.string.receipt_list_child_amount,
                            AppUtils.currencyFormatter().format(receiptData.getSplitTotal())));
            int spitCount = receiptData.getSplitCount();
            if(spitCount > 1)
            {
                holder.splitCout.setText("+ "+String.valueOf(spitCount));
                holder.splitCountImgView.setVisibility(View.VISIBLE);
            }
            return convertView;
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "IndexOutOfBoundsException " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getLocalizedMessage(), e);
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
            holder.amount.setText(context.getString(R.string.receipt_list_header_amount, AppUtils.currencyFormatter().format(headerData.getTotal())));
            return convertView;

        } catch (ParseException e) {
            Log.e(TAG, "ParseException " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e.getLocalizedMessage(), e);
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
        View expenseTag;
        TextView bizName;
        TextView date;
        TextView amount;
        TextView splitCout;
        ImageView splitCountImgView;
    }
}
