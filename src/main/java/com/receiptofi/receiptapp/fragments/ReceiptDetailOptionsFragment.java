package com.receiptofi.receiptapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.receiptofi.receiptapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptDetailOptionsFragment extends Fragment {


    public ReceiptDetailOptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.f_receipt_detail_options, container, false);
    }

}
