package com.receiptofi.receiptapp.http;

import okhttp3.Headers;

public interface ResponseHandler {

    void onSuccess(Headers headers, String body);

    void onError(int statusCode, String error);

    void onException(Exception exception);
}


