package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ShoppingListActivity;
import com.receiptofi.receiptapp.model.ItemReceiptModel;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.db.ItemReceiptUtils;
import com.receiptofi.receiptapp.utils.db.ShoppingItemUtils;

import java.util.List;

/**
 * User: hitender
 * Date: 12/11/15 12:29 AM
 */
public class ShoppingListAdapter extends ArrayAdapter<ShoppingItemModel> {
    private static final String TAG = ReceiptItemListAdapter.class.getSimpleName();

    private Context context;
    private List<ShoppingItemModel> list;
    private ShoppingListActivity shoppingListActivity;

    public ShoppingListAdapter(Context context, ShoppingListActivity shoppingListActivity, List<ShoppingItemModel> list) {
        super(context, R.layout.fragment_shopping_list_item, list);
        this.context = context;
        this.shoppingListActivity = shoppingListActivity;
        this.list = list;
    }

    public void updateList(List<ShoppingItemModel> shoppingItemModels) {
        this.list = shoppingItemModels;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ShoppingItemModel getItem(int position) {
        return list.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;
        try {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                view = inflater.inflate(R.layout.fragment_shopping_list_item, parent, false);

                final ViewHolder holder = new ViewHolder();
                holder.itemName = (TextView) view.findViewById(R.id.shopping_list_item_name);
                holder.price = (TextView) view.findViewById(R.id.shopping_list_price);
                holder.quantity = (TextView) view.findViewById(R.id.shopping_list_quantity);
                holder.checkbox = (CheckBox) view.findViewById(R.id.shopping_list_checkBox);
                holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        ShoppingItemModel element = (ShoppingItemModel) holder.checkbox.getTag();
                        if (buttonView.isChecked()) {
                            boolean updateSuccess = ShoppingItemUtils.updateCheckCondition(
                                    list.get(position).getBizName(),
                                    list.get(position).getName(),
                                    true);

                            element.check();
                        } else {
                            boolean updateSuccess = ShoppingItemUtils.updateCheckCondition(
                                    list.get(position).getBizName(),
                                    list.get(position).getName(),
                                    false);

                            element.unCheck();
                        }
                        notifyDataSetChanged();
                    }
                });

                view.setTag(holder);
                holder.checkbox.setTag(list.get(position));
            } else {
                view = convertView;
                /** This line is important. */
                ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
            }

            ViewHolder holder = (ViewHolder) view.getTag();
            holder.checkbox.setChecked(list.get(position).isChecked());
            if (list.get(position).isChecked()) {
                holder.price.setTextColor(context.getResources().getColor(R.color.tv_black_second));
                holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                holder.itemName.setTextColor(context.getResources().getColor(R.color.tv_black_second));
                holder.itemName.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.price.setTextColor(context.getResources().getColor(R.color.black));
                holder.price.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                holder.itemName.setTextColor(context.getResources().getColor(R.color.black));
                holder.itemName.setPaintFlags(holder.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            holder.itemName.setText(list.get(position).getName());
            List<ItemReceiptModel> itemReceiptModels = ItemReceiptUtils.latestItemReceiptModel(list.get(position).getBizName(), list.get(position).getName());
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
        } catch (Exception e) {
            Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
        return view;
    }

    private void sendViewUpdateMessage(int elementPosition) {
        MessageShopping messageShopping = new MessageShopping();
        messageShopping
                .setPosition(elementPosition)
                .setBizName(list.get(elementPosition).getBizName())
                .setChecked(list.get(elementPosition).isChecked());

        Message msg = new Message();
        msg.what = ShoppingListActivity.UPDATE_SHOPPING_LIST;
        msg.obj = messageShopping;

        shoppingListActivity.updateHandler.sendMessage(msg);
    }

    private class ViewHolder {
        TextView itemName;
        TextView price;
        TextView quantity;
        CheckBox checkbox;
    }

    public class MessageShopping {
        int position;
        String bizName;
        boolean checked;

        public int getPosition() {
            return position;
        }

        public MessageShopping setPosition(int position) {
            this.position = position;
            return this;
        }

        public String getBizName() {
            return bizName;
        }

        public MessageShopping setBizName(String bizName) {
            this.bizName = bizName;
            return this;
        }

        public boolean isChecked() {
            return checked;
        }

        public MessageShopping setChecked(boolean checked) {
            this.checked = checked;
            return this;
        }
    }
}
