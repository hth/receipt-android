package com.receiptofi.android.fragments;

import com.receiptofi.android.HomePageActivity;
import com.receiptofi.android.R;
import com.receiptofi.android.adapters.ReceiptListAdapter;
import com.receiptofi.android.models.ReceiptModel;
import com.receiptofi.android.utils.ReceiptUtils;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
            ((HomePageActivity) context).invokeDetailReceiptView(view, model);
        }
    };
}
