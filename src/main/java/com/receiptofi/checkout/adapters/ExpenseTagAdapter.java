package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ExpenseTagModel;

import java.util.List;

/**
 * Created by kevin on 5/26/15.
 */
public class ExpenseTagAdapter extends BaseAdapter {

    /**
     * New implementation for list style ExpenseTag
     */
    private List<ExpenseTagModel> mList;
    private Context mContext;

    public ExpenseTagAdapter(Context context, List<ExpenseTagModel> tags) {
        this.mContext = context;
        this.mList = tags;
    }

    public void updateList(List<ExpenseTagModel> mlistHashMaps) {
        this.mList = mlistHashMaps;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(mContext,
                    R.layout.fragment_expense_tag_style_listview_item, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ExpenseTagModel tagModel = (ExpenseTagModel) getItem(i);
        holder.tv_content.setText(tagModel.getName());
        holder.tv_content.setTextColor(Color.parseColor(tagModel.getColor()));
        holder.iv_label.setBackgroundColor(Color.parseColor(tagModel.getColor()));
        return view;
    }

    class ViewHolder {
        ImageView iv_label;
        TextView tv_content;

        public ViewHolder(View view) {
            iv_label = (ImageView) view.findViewById(R.id.tag_color);
            tv_content = (TextView) view.findViewById(R.id.tv_tag);
            view.setTag(this);
        }
    }
}

