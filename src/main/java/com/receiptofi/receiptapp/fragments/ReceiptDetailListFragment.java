package com.receiptofi.receiptapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.receiptofi.receiptapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptDetailListFragment extends Fragment {


    private View view;

    public ReceiptDetailListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.f_receipt_detail_list, container, false);
        return view;
    }

}
