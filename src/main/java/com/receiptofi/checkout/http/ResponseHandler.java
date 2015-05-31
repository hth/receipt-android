package com.receiptofi.checkout.http;

import org.apache.http.Header;

public interface ResponseHandler {

    void onSuccess(Header[] headers, String body);

    void onError(int statusCode, String error);

    void onException(Exception exception);
}


