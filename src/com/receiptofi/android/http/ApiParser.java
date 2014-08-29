package com.receiptofi.android.http;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.receiptofi.android.models.ReceiptModel;

public class ApiParser {

	public static void getLoginDetails(String response) {
		try {
			JSONObject loginResponseJson = new JSONObject(response);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<ReceiptModel> getReceipts(String response) {

		ArrayList<ReceiptModel> models = new ArrayList<ReceiptModel>();
		// ArrayList<Rec>
		try {
			JSONArray array = new JSONArray(response);
			for (int i = 0; i < array.length(); i++) {
				JSONObject json = array.getJSONObject(i);
				ReceiptModel model = new ReceiptModel();
				// ReceiptModel.bizName.class;

				JSONObject bizNameJson = json.getJSONObject("bizName");
				model.bizName = bizNameJson.getString("name");

				JSONObject bizStoreJson = json.getJSONObject("bizStore");
				model.bizStoreAddress = bizStoreJson.getString("address");
				model.bizStorePhone = bizStoreJson.getString("phone");

				model.date = json.getString("date");
				model.expenseReport = json.getString("expenseReport");

				JSONArray jsonArray = json.getJSONArray("files");

				JSONObject filesJson = (JSONObject) jsonArray.get(0);
				model.filesBlobId = filesJson.getString("blobId");
				model.filesOrientation = filesJson.getString("orientation");
				model.filesSequence = filesJson.getString("sequence");

				model.id = json.getString("id");

				JSONObject notesJson = json.getJSONObject("notes");
				model.notesText = notesJson.getString("text");

				String ptaxStr = json.getString("ptax");
				if (ptaxStr != null) {
					model.ptax = Double.valueOf(ptaxStr);
				}
				String ridStr = json.getString("rid");
				if (ridStr != null) {
					model.rid = Long.valueOf(ridStr);
				}
				Double totalStr = json.getDouble("total");
				if (totalStr != null) {
					model.total = totalStr.doubleValue();
				}
				models.add(model);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return models;
	}

}
