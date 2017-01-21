package com.receiptofi.receiptapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ExpenseTagModel;

import java.util.List;

/**
 * Created by Omkar Gharat on 12/24/2016.
 */

public class ExpenseTagAdapterRecycleView extends RecyclerView.Adapter {

    private  List<ExpenseTagModel> list;
    private Context mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expensetaglistitem,parent,false);
        return new ExpenseTagViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ExpenseTagModel model = list.get(position);
        holder.itemView.setBackgroundColor(Color.parseColor(model.getColor()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ExpenseTagViewHolder extends RecyclerView.ViewHolder
    {
        public View colorTag;
        public ExpenseTagViewHolder(View view)
        {
            super(view);
            colorTag = (View)view.findViewById(R.id.expenseTagColorView);

        }
    }

    public ExpenseTagAdapterRecycleView(Context context, List<ExpenseTagModel> expenseTagModelList)
    {
        this.list = expenseTagModelList;
        mContext = context;
    }
}
