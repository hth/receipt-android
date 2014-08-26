package com.receiptofi.android.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Fragment;
import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.receiptofi.android.R;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPutils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.utils.UserUtils;

public class ReceiptListFragment extends Fragment {

	Context context;
	View screen;
	ListView recepitList;

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
		fetchReceipts();
		
	}
	
	private void fetchReceipts(){
		ArrayList<NameValuePair> headerData= new ArrayList<NameValuePair>();
		headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
		headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));
		
		HTTPutils.AsyncRequest(headerData, API.GET_ALL_RECEIPTS, HTTPutils.HTTP_METHOD_GET, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				
			}
			
			@Override
			public void onExeption(Exception exception) {
				
			}
			
			@Override
			public void onError(String Error) {
				
			}
		});
	}
	
	
}
