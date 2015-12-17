package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ItemReceiptModel;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.db.ItemReceiptUtils;

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
                holder.itemName = (TextView) convertView.findViewById(R.id.shopping_list_item_name);
                holder.price = (TextView) convertView.findViewById(R.id.shopping_list_price);
                holder.quantity = (TextView) convertView.findViewById(R.id.shopping_list_quantity);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ShoppingItemModel itemModel = getItem(position);
            holder.itemName.setText(itemModel.getName());
            List<ItemReceiptModel> itemReceiptModels = ItemReceiptUtils.latestItemReceiptModel(itemModel.getBizName(), itemModel.getName());
            if (!itemReceiptModels.isEmpty()) {
                ItemReceiptModel itemReceiptModel = itemReceiptModels.get(0);

                /**
                 * Condition below is similar to Receipt Item with price and quantity
                 * @see com.receiptofi.receiptapp.adapters.ReceiptItemListAdapter#getView(int, View, ViewGroup)
                 */
                if (Double.parseDouble(itemReceiptModel.getQuantity()) != 1) {
                    holder.quantity.setText(context.getString(
                            R.string.rd_item_quantity,
                            Double.parseDouble(itemReceiptModel.getQuantity()),
                            AppUtils.currencyFormatter().format(itemReceiptModel.getPrice())));

                    holder.price.setText(context.getString(
                            R.string.rd_item_price,
                            AppUtils.currencyFormatter().format((itemReceiptModel.getPrice() * Double.parseDouble(itemReceiptModel.getQuantity())))));
                } else {
                    holder.quantity.setVisibility(View.GONE);
                    holder.price.setText(context.getString(R.string.rd_item_price, AppUtils.currencyFormatter().format(itemReceiptModel.getPrice())));
                }
            }
            return convertView;
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    private class ViewHolder {
        TextView itemName;
        TextView price;
        TextView quantity;
    }
}
