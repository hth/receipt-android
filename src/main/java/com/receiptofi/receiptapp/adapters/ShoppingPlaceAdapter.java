package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;

import java.util.List;

/**
 * User: hitender
 * Date: 12/8/15 5:07 AM
 */
public class ShoppingPlaceAdapter extends BaseAdapter {
    private List<ShoppingPlace> shoppingPlaces;
    private Context context;

    public ShoppingPlaceAdapter(Context context, List<ShoppingPlace> shoppingPlaces) {
        this.context = context;
        this.shoppingPlaces = shoppingPlaces;
    }

    public void updateList(List<ShoppingPlace> shoppingPlaces) {
        this.shoppingPlaces = shoppingPlaces;
    }

    @Override
    public int getCount() {
        return shoppingPlaces.size();
    }

    @Override
    public Object getItem(int i) {
        return shoppingPlaces.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view) {
            view = View.inflate(context, R.layout.shopping_place_list, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ShoppingPlace shoppingPlace = (ShoppingPlace) getItem(i);
        holder.bizName.setText(shoppingPlace.getBizName());

        if (shoppingPlace.getLastShopped().isEmpty()) {
            holder.lastShopped.setText("");
        } else {
            holder.lastShopped.setText(
                    context.getResources().getString(R.string.shopped,
                            NotificationAdapter.prettyTime.format(shoppingPlace.getLastShopped().get(0))));
        }
        return view;
    }

    class ViewHolder {
        TextView bizName;
        TextView lastShopped;

        public ViewHolder(View view) {
            bizName = (TextView) view.findViewById(R.id.biz_name);
            lastShopped = (TextView) view.findViewById(R.id.last_shopped);
            view.setTag(this);
        }
    }
}
