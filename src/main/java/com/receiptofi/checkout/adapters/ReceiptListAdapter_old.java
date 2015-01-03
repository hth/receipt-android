package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.models.ReceiptModel;

import java.util.ArrayList;

public class ReceiptListAdapter_old extends ArrayAdapter<ReceiptModel> {

    private Context context;
    private ArrayList<ReceiptModel> models;

    public ReceiptListAdapter_old(Context context, ArrayList<ReceiptModel> models) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        this.context = context;
        this.models = models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.receipt_list_row, null);
        }

        ReceiptModel receipt = models.get(position);
        if (receipt.bizName != null) {
            ((TextView) convertView.findViewById(R.id.bizNameText)).setText(receipt.bizName);
        }

        if (receipt.ptax != 0.0f) {
            ((TextView) convertView.findViewById(R.id.ptaxText)).setText(String.valueOf(receipt.ptax));
        }

        if (receipt.total != 0.0f) {
            ((TextView) convertView.findViewById(R.id.totalText)).setText(String.valueOf(receipt.total));
        }

        if (receipt.date != null) {
            ((TextView) convertView.findViewById(R.id.dateText)).setText(receipt.date);
        }

        convertView.setTag(receipt);
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return models.size();
    }

    @Override
    public ReceiptModel getItem(int position) {
        // TODO Auto-generated method stub
        return models.get(position);
    }
}
