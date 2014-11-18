package com.receiptofi.android.fragments;

import com.receiptofi.android.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ActionFragment extends ParentFragment {

    Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View homeScreen = inflater.inflate(R.layout.action_page, null);
        homeScreen.findViewById(R.id.back).setVisibility(View.GONE);
        homeScreen.findViewById(R.id.menu).setVisibility(View.VISIBLE);
        return homeScreen;
    }


}
