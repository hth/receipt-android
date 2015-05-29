package com.receiptofi.checkout.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.checkout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 5/26/15.
 */
public class MenuListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater = null;

    // create menu items;
    private String titles[] = {
            "Home",
            "Notification",
            "Tag Modify",
            "Billing",
            "Setting"
    };

    private List<IconDrawable> icons_array = new ArrayList<>();

    public MenuListAdapter(Activity activity) {
        this.activity = activity;
        setupData();
        inflater = LayoutInflater.from(activity);
    }

    private void setupData(){
        icons_array.add(new IconDrawable(activity, Iconify.IconValue.fa_home)
                .colorRes(R.color.white)
                .actionBarSize());
        icons_array.add(new IconDrawable(activity, Iconify.IconValue.fa_bell_o)
                .colorRes(R.color.white)
                .actionBarSize());
        icons_array.add(new IconDrawable(activity, Iconify.IconValue.fa_tags)
                .colorRes(R.color.white)
                .actionBarSize());
        icons_array.add(new IconDrawable(activity, Iconify.IconValue.fa_shopping_cart)
                .colorRes(R.color.white)
                .actionBarSize());
        icons_array.add(new IconDrawable(activity, Iconify.IconValue.fa_cogs)
                .colorRes(R.color.white)
                .actionBarSize());
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return titles[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.menu_list_item, null);
            mHolder = new ViewHolder();
            mHolder.menu_icon = (ImageView) view.findViewById(R.id.menu_ic);
            mHolder.menu_name = (TextView) view.findViewById(R.id.menu_text);
            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }
        mHolder.menu_name.setText((CharSequence)titles[i]);
//        mHolder.menu_icon.setBackgroundResource(icon[i]);
        mHolder.menu_icon.setImageDrawable(icons_array.get(i));
        return view;
    }

    class ViewHolder {
        ImageView menu_icon;
        TextView menu_name;
    }
}
