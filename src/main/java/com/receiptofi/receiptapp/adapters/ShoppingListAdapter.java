package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ShoppingItemModel;

import java.util.List;

/**
 * User: hitender
 * Date: 12/11/15 12:29 AM
 */
public class ShoppingListAdapter extends ArrayAdapter<ShoppingItemModel> {
    private static final String TAG = ReceiptItemListAdapter.class.getSimpleName();

    private final LayoutInflater inflater;
    private Context context;
    private List<ShoppingItemModel> rdItems;

    public ShoppingListAdapter(Context context, List<ShoppingItemModel> items) {
        super(context, R.layout.fragment_shopping_list_item, items);
        this.context = context;
        this.rdItems = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rdItems.size();
    }

    @Override
    public ShoppingItemModel getItem(int position) {
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
                convertView = inflater.inflate(R.layout.fragment_shopping_list_item, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.tag_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ShoppingItemModel itemModel = getItem(position);
            holder.title.setText(itemModel.getName());
            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private class ViewHolder {
        TextView title;
    }
}
