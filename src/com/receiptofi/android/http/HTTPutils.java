package com.receiptofi.android.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPutils {

	public static String HTTP_METHOD_POST = "POST";

	public static String HTTP_METHOD_GET = "GET";

	private static final String CONNECTION_URL_STAGING = "https://receiptofi.com:9443/receipt-mobile";

	private static final String CONNECTION_URL_PRODUCTION = "https://receiptofi.com/receipt-mobile";

	private static final String URL = CONNECTION_URL_STAGING;

	public static String getPostResponse(ArrayList<NameValuePair> params)
			throws Exception {

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);

		httpPost.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse response = client.execute(httpPost);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		StringBuffer responseBuffer = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			responseBuffer.append(line);
		}

		return responseBuffer.toString();
	}

	public static String getResponse(ArrayList<NameValuePair> params)
			throws Exception {
		StringBuilder getParamString = new StringBuilder();
		HttpClient client = new DefaultHttpClient();

		if (params != null) {

			for (NameValuePair keyval : params) {
				getParamString.append("&").append(keyval.getName() + "=")
						.append(keyval.getValue());
			}
		}
		HttpGet httpGet = new HttpGet(URL + getParamString.toString());
		HttpResponse response = client.execute(httpGet);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		StringBuffer responseBuffer = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			responseBuffer.append(line);
		}

		return responseBuffer.toString();

	}

	public static void AsyncRequest(ArrayList<NameValuePair> params,
			String HTTP_method, ResponseHandler handler) {
		String response = null;
		try {
			if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_POST)) {
				response=getPostResponse(params);
			}else if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_GET)) {
				response=getResponse(params);
			}
			handler.onSuccess(response);
		} catch (Exception e) {
			handler.onExeption(e);
		}

	}

}
