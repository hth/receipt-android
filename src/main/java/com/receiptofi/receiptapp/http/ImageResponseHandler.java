package com.receiptofi.receiptapp.http;

import com.receiptofi.receiptapp.model.ImageModel;

public interface ImageResponseHandler {

    void onSuccess(ImageModel iModel, String response);

    void onError(ImageModel iModel, String Error);

    void onException(ImageModel iModel, Exception exception);
}
