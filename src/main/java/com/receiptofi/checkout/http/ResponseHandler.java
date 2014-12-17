package com.receiptofi.checkout.http;

import org.apache.http.Header;

public interface ResponseHandler {

    public void onSuccess(Header[] headers);

    public void onError(int statusCode, String error);

    public void onException(Exception exception);

}


