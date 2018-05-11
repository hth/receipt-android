package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ExpenseTagModel;

import java.util.List;

/**
 * User: kevin
 * Date: 5/26/15 3:57 PM
 */
public class ExpenseTagAdapter extends BaseAdapter {

    /**
     * New implementation for list style ExpenseTag
     */
    private List<ExpenseTagModel> expenseTagModels;
    private Context context;

    public ExpenseTagAdapter(Context context, List<ExpenseTagModel> expenseTagModels) {
        this.context = context;
        this.expenseTagModels = expenseTagModels;
    }

    public void updateList(List<ExpenseTagModel> expenseTagModels) {
        this.expenseTagModels = expenseTagModels;
    }

    @Override
    public int getCount() {
        return expenseTagModels.size();
    }

    @Override
    public Object getItem(int i) {
        return expenseTagModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view) {
            view = View.inflate(context, R.layout.listitem_expense_tag, null);
            new ViewHolder(view);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        ExpenseTagModel tagModel = (ExpenseTagModel) getItem(i);
        holder.tag.setText(tagModel.getTag());
        holder.tag.setTextColor(context.getResources().getColor(R.color.tv_black));
        holder.color.setBackgroundColor(Color.parseColor(tagModel.getColor()));
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "delete pressed", Toast.LENGTH_LONG).show();
            }
        });
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "edit pressed", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    class ViewHolder {
        private ImageView color;
        private TextView tag;
        private ImageView iv_edit;
        private ImageView iv_delete;

        public ViewHolder(View view) {
            color = (ImageView) view.findViewById(R.id.tag_color);
            tag = (TextView) view.findViewById(R.id.tag_name);
            iv_edit = (ImageView) view.findViewById(R.id.iv_edit);
            iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
            view.setTag(this);
        }
    }
}

