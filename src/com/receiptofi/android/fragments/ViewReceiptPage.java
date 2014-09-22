package com.receiptofi.android.fragments;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.receiptofi.android.HomePageActivity;
import com.receiptofi.android.R;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.models.RecieptElement;
import com.receiptofi.android.utils.UserUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ViewReceiptPage extends Fragment{

	View screen;
	Context context;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		context=getActivity();
		
		String receiptId=null;
		if(getArguments()!=null && getArguments().getString("receiptId")!=null){
			receiptId=getArguments().getString("receiptId");
		}
		
		((HomePageActivity)context).showLoader("Fetching receipt details");
		
		ArrayList<NameValuePair> headerData = new ArrayList<NameValuePair>();
		headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
		headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));
		
		HTTPUtils.AsyncRequest(headerData, API.VIEW_RECEIPT_DETAIL+receiptId+".json", HTTPUtils.HTTP_METHOD_GET, new ResponseHandler() {
			
			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				((HomePageActivity)context).hideLoader();
				ArrayList<RecieptElement> elements=	ResponseParser.getReceiptDetails(response);
				displayElements(elements);
			}
			
			

			@Override
			public void onExeption(Exception exception) {
				// TODO Auto-generated method stub
				((HomePageActivity)context).hideLoader();
			}
			
			@Override
			public void onError(String Error) {
				// TODO Auto-generated method stub
				((HomePageActivity)context).hideLoader();
			}
		});
		
		

	}
	
	
	private void displayElements(ArrayList<RecieptElement> elements) {
		// TODO Auto-generated method stub
		if(elements!=null){
			
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final ViewGroup elementsContainer= (ViewGroup) ((Activity)context).findViewById(R.id.receiptElements);
			
			for(RecieptElement element : elements){
				final View view =inflator.inflate(R.layout.receiptdetail_element_row, null);
				((TextView)view.findViewById(R.id.elementName)).setText(element.name);
				((TextView)view.findViewById(R.id.elemntPrice)).setText(element.price);
				((HomePageActivity)getActivity()).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						elementsContainer.addView(view);
					}
				});
			}
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.receipt_detail, null);
		screen=view;
		return screen;
	}	
}
