package com.receiptofi.checkout.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ReceiptModel;

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
        if (receipt.getBizName() != null) {
            ((TextView) convertView.findViewById(R.id.bizNameText)).setText(receipt.getBizName());
        }

        if (receipt.getPtax() != 0.0f) {
            ((TextView) convertView.findViewById(R.id.ptaxText)).setText(String.valueOf(receipt.getPtax()));
        }

        if (receipt.getTotal() != 0.0f) {
            ((TextView) convertView.findViewById(R.id.totalText)).setText(String.valueOf(receipt.getTotal()));
        }

        if (receipt.getDate() != null) {
            ((TextView) convertView.findViewById(R.id.dateText)).setText(receipt.getDate());
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
