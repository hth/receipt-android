package com.receiptofi.android.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.receiptofi.android.R;
import com.receiptofi.android.adapters.ReceiptListAdapter;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.models.ReceiptModel;
import com.receiptofi.android.utils.ReceiptUtils;
import com.receiptofi.android.utils.UserUtils;

public class ReceiptListFragment extends Fragment {

	Context context;
	View screen;
	ListView recepitList;
	ReceiptListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.receipt_list, null);
		recepitList=(ListView)view.findViewById(R.id.reciptListView);
		view.findViewById(R.id.back).setVisibility(View.VISIBLE);
		view.findViewById(R.id.menu).setVisibility(View.GONE);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		adapter= new ReceiptListAdapter(context, ReceiptUtils.getAllReciepts());
		recepitList.setAdapter(adapter);
	}
	
	
}
