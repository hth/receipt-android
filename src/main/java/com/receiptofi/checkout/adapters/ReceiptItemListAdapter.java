package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ReceiptItemModel;

import java.util.List;

/**
 * Created by PT on 1/18/15.
 */
public class ReceiptItemListAdapter extends ArrayAdapter<ReceiptItemModel> {

    private static final String TAG = ReceiptItemListAdapter.class.getSimpleName();

    private Context context;
    private final LayoutInflater inflater;
    private List<ReceiptItemModel> rdItems;

    public ReceiptItemListAdapter(Context context, List<ReceiptItemModel> items) {
        super(context, R.layout.rd_list_item, items);
        this.context = context;
        this.rdItems = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rdItems.size();
    }

    @Override
    public ReceiptItemModel getItem(int position) {
        return rdItems.get(position);
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
            if(convertView == null){
                convertView = inflater.inflate(R.layout.rd_list_item, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.rd_list_item_title);
                holder.dscp = (TextView) convertView.findViewById(R.id.rd_list_item_title_dscp);
                holder.amount = (TextView) convertView.findViewById(R.id.rd_list_item_amount);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ReceiptItemModel itemModel = getItem(position);
            holder.title.setText(itemModel.getName());
            holder.dscp.setText(itemModel.getQuantity());
            holder.amount.setText(context.getString(R.string.rd_item_amount, itemModel.getPrice()));

            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private class ViewHolder {
        TextView title;
        TextView dscp;
        TextView amount;
    }
}
