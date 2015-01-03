package com.receiptofi.checkout.http;

import com.receiptofi.checkout.model.ImageModel;

public interface ImageResponseHandler {

    public void onSuccess(ImageModel iModel, String response);

    public void onError(ImageModel iModel, String Error);

    public void onException(ImageModel iModel, Exception exception);
}
