package com.receiptofi.receipts.http;

import com.squareup.okhttp.Headers;

public interface ResponseHandler {

    void onSuccess(Headers headers, String body);

    void onError(int statusCode, String error);

    void onException(Exception exception);
}


