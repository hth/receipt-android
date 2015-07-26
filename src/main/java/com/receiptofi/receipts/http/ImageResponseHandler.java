package com.receiptofi.receipts.http;

import com.receiptofi.receipts.model.ImageModel;

public interface ImageResponseHandler {

    void onSuccess(ImageModel iModel, String response);

    void onError(ImageModel iModel, String Error);

    void onException(ImageModel iModel, Exception exception);
}
