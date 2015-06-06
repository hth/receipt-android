package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ExpenseTagModel;

import java.util.List;

/**
 * Created by kevin on 5/26/15.
 */
public class ExpenseTagAdapter extends BaseAdapter {
    private List<ExpenseTagModel> mList;
    private Context mContext;
    public ExpenseTagAdapter(Context context, List<ExpenseTagModel> tags) {

        this.mContext = context;
        this.mList = tags;
    }

    public void updateList( List<ExpenseTagModel> mlistHashMaps) {
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
//        Log.i("Kevin", "in getView: i:" + i + ". And the tag is:" + mList.get(i).getTag());
        if (view == null) {
            view = View.inflate(mContext,
                    R.layout.fragment_expense_tag_list_item, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ExpenseTagModel tagModel = (ExpenseTagModel)getItem(i);
        int paddingLeft = holder.btn_tag.getPaddingLeft();
        int paddingRight = holder.btn_tag.getPaddingRight();
        int paddingTop = holder.btn_tag.getPaddingTop();
        int paddingBottom = holder.btn_tag.getPaddingBottom();

        holder.btn_tag.setText(" x  " + tagModel.getName());
        holder.btn_tag.setTextColor(Color.parseColor(tagModel.getColor()));
        holder.btn_tag.setPadding(paddingLeft, paddingRight, paddingTop, paddingBottom);
        return view;
    }

    class ViewHolder {
        TextView btn_tag;

        public ViewHolder(View view) {
            btn_tag = (TextView) view.findViewById(R.id.btn_tag);
            view.setTag(this);
        }
    }

}

