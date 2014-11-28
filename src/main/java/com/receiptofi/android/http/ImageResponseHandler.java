package com.receiptofi.android.http;

import com.receiptofi.android.models.ImageModel;

public interface ImageResponseHandler {

    public void onSuccess(ImageModel iModel, String response);

    public void onError(ImageModel iModel, String Error);

    public void onException(ImageModel iModel, Exception exception);
}
