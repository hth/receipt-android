package com.receiptofi.android.http;

import java.io.File;

public interface DownloadImageResponseHandler {
    public void onSuccess(File file, String response);

    public void onError(File file, String Error);

    public void onException(Exception exception);
}
