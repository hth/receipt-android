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
 * User: PT
 * Date: 1/18/15 11:11 PM
 */
public class ReceiptItemListAdapter extends ArrayAdapter<ReceiptItemModel> {

    private static final String TAG = ReceiptItemListAdapter.class.getSimpleName();
    private final LayoutInflater inflater;
    private Context context;
    private List<ReceiptItemModel> rdItems;

    public ReceiptItemListAdapter(Context context, List<ReceiptItemModel> items) {
        super(context, R.layout.rd_item_list, items);
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
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.rd_item_list, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.rd_list_item_title);
                holder.quantity = (TextView) convertView.findViewById(R.id.rd_list_item_quantity);
                holder.price = (TextView) convertView.findViewById(R.id.rd_list_item_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ReceiptItemModel itemModel = getItem(position);
            holder.title.setText(itemModel.getName());
            if (Double.parseDouble(itemModel.getQuantity()) > 1) {
                holder.quantity.setText(context.getString(R.string.rd_item_quantity, Double.parseDouble(itemModel.getQuantity()), Double.parseDouble(itemModel.getPrice())));
                holder.price.setText(context.getString(R.string.rd_item_price, (Double.parseDouble(itemModel.getPrice()) * Double.parseDouble(itemModel.getQuantity()))));
            } else {
                holder.quantity.setVisibility(View.GONE);
                holder.price.setText(context.getString(R.string.rd_item_price, Double.parseDouble(itemModel.getPrice())));
            }


            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private class ViewHolder {
        TextView title;
        TextView quantity;
        TextView price;
    }
}
