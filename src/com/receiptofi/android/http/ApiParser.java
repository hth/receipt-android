package com.receiptofi.android.http;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiParser {

	public static void getLoginDetails(String response) {
		try {
			JSONObject loginResponseJson = new JSONObject(response);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
