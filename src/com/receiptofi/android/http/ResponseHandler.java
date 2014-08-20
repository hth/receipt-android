package com.receiptofi.android.http;

public interface ResponseHandler {

	public void onSuccess(String response);
	public void onError(String Error);
	public void onExeption(Exception exception);

}


