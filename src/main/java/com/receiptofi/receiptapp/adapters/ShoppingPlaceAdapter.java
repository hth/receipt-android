package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.model.helper.ShoppingPlace;

import java.util.List;

/**
 * User: hitender
 * Date: 12/8/15 5:07 AM
 */
public class ShoppingPlaceAdapter extends BaseAdapter {
    /**
     * New implementation for list style ExpenseTag
     */
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
            view = View.inflate(context, R.layout.fragment_shopping_places_style_list, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ShoppingPlace tagModel = (ShoppingPlace) getItem(i);
        holder.tagName.setText(tagModel.getBizName());
        holder.tagName.setTextColor(context.getResources().getColor(R.color.tv_black));
        holder.tagColor.setBackgroundColor(context.getResources().getColor(R.color.tv_black));
        return view;
    }

    class ViewHolder {
        ImageView tagColor;
        TextView tagName;

        public ViewHolder(View view) {
            tagColor = (ImageView) view.findViewById(R.id.tag_color);
            tagName = (TextView) view.findViewById(R.id.tag_name);
            view.setTag(this);
        }
    }
}
