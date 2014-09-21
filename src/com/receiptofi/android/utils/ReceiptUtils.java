package com.receiptofi.android.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.database.Cursor;

import com.receiptofi.android.ReceiptofiApplication;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.models.ReceiptDB;
import com.receiptofi.android.models.ReceiptModel;

public class ReceiptUtils {

	public static void fetchReceiptsAndSave() {
		
		ArrayList<NameValuePair> headerData = new ArrayList<NameValuePair>();
		headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
		headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));

		HTTPUtils.AsyncRequest(headerData, API.GET_ALL_RECEIPTS,
				HTTPUtils.HTTP_METHOD_GET, new ResponseHandler() {

					@Override
					public void onSuccess(String response) {
						ArrayList<ReceiptModel> models = ResponseParser.getReceipts(response);
						for (ReceiptModel model : models) {
							model.save();
						}
					}

					@Override
					public void onExeption(Exception exception) {

					}

					@Override
					public void onError(String Error) {

					}
				});
	}
	
	public static void clearReceipts(){
		ReceiptofiApplication.rdh.getWritableDatabase().rawQuery("delete from "+ReceiptDB.Receipt.TABLE_NAME+";", null);
	}
	
	public static ArrayList<ReceiptModel> getAllReciepts() {

		String[] coloumns = new String[] { ReceiptDB.Receipt.BIZ_NAME,ReceiptDB.Receipt.DATE_R, ReceiptDB.Receipt.P_TAX,ReceiptDB.Receipt.TOTAL };
		Cursor recieptsRecords = ReceiptofiApplication.rdh.getReadableDatabase().query(ReceiptDB.Receipt.TABLE_NAME, coloumns, null, null,	null, null, null);
		
		ArrayList<ReceiptModel> rModels =new ArrayList<ReceiptModel>();
		if (recieptsRecords != null && recieptsRecords.getCount() > 0) {
			for (recieptsRecords.moveToFirst(); !recieptsRecords.isAfterLast(); recieptsRecords.moveToNext()) {
				ReceiptModel model = new ReceiptModel();
				model.bizName = recieptsRecords.getString(0);
				model.date = recieptsRecords.getString(1);
				model.ptax = recieptsRecords.getDouble(2);
				model.total = recieptsRecords.getDouble(3);
				rModels.add(model);
			}

		}
		recieptsRecords.close();
		recieptsRecords=null;
		return rModels;

	}
	
}
