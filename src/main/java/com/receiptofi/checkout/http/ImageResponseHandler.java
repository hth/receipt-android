package com.receiptofi.checkout.http;

import com.receiptofi.checkout.model.ImageModel;

public interface ImageResponseHandler {

    void onSuccess(ImageModel iModel, String response);

    void onError(ImageModel iModel, String Error);

    void onException(ImageModel iModel, Exception exception);
}
