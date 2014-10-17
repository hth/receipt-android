package com.receiptofi.android.services;

import com.receiptofi.android.http.API;
import com.receiptofi.android.http.DownloadImageResponseHandler;
import com.receiptofi.android.utils.AppUtils;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class ImageDownloadService {
    private volatile File file;
    private DownloadImageResponseHandler responseHandler;
    private String uri;
    private String blobId;

    public ImageDownloadService(String blobId, String uri, DownloadImageResponseHandler responseHandler) {
        this.blobId = blobId;
        this.uri = uri;
        this.responseHandler = responseHandler;
    }

    private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {

        @Override
        protected Long doInBackground(URL... urls) {
            FileWriter imageWriter = null;
            BufferedReader reader = null;
            char[] bufferSize = new char[128];  //smaller memory footprint
            try {
                HttpGet httpGet = new HttpGet(urls[0].toURI());
                httpGet.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                httpGet.addHeader(API.key.XR_MAIL, UserUtils.getEmail());
                HttpResponse response = new DefaultHttpClient().execute(httpGet);
                switch(response.getStatusLine().getStatusCode()) {
                    case 200:
                        Header[] header = response.getHeaders("Content-Type");
                        if (header[0].getValue().equalsIgnoreCase("image/jpeg")) {
                            file = new File(AppUtils.getImageDir() + File.separator + blobId + ".jpg");
                        }
                        //other conditions for PNG and GIF
                        imageWriter = new FileWriter(file);

                        reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        while (reader.read(bufferSize) != -1) {
                            imageWriter.write(bufferSize, 0, bufferSize.length);
                        }
                        responseHandler.onSuccess(file, null);

                        break;
                    default:
                        //TODO(hth) log error
                        break;
                }
            } catch (Exception e) {
                responseHandler.onException(e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (imageWriter != null) {
                        imageWriter.close();
                    }
                } catch (IOException e) {
                    Log.e("error closing file", e.getMessage());
                    //Do nothing
                }
            }
            return file.length();
        }
    }

    public void fetchFile(URL url) {
        new DownloadFilesTask().execute(url);
    }
}
