package com.receiptofi.checkout.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.receiptofi.checkout.HomePageActivity_OLD;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.ReceiptListAdapter;
import com.receiptofi.checkout.models.ReceiptModel;
import com.receiptofi.checkout.dbutils.ReceiptUtils;

public class ReceiptListFragment extends Fragment {

    Context context;
    View screen;
    ListView receiptList;
    ReceiptListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.receipt_list, null);
        receiptList = (ListView) view.findViewById(R.id.reciptListView);
        view.findViewById(R.id.back).setVisibility(View.VISIBLE);
        view.findViewById(R.id.menu).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        adapter = new ReceiptListAdapter(context, ReceiptUtils.getAllReciepts());
        receiptList.setAdapter(adapter);
        receiptList.setOnItemClickListener(receiptListener);
    }

    OnItemClickListener receiptListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            ReceiptModel model = (ReceiptModel) view.getTag();
            ((HomePageActivity_OLD) context).invokeDetailReceiptView(view, model);
        }
    };
}
