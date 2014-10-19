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
    private static final String TAG = ImageDownloadService.class.getSimpleName();
    private File file;
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
                        file = new File(
                                AppUtils.getImageDir() +
                                        File.separator +
                                        blobId +
                                        (header[0].getValue() != null ? findFileExtension(header[0].getValue()) : ".jpg")
                        );
                        imageWriter = new FileWriter(file);

                        reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        while (reader.read(bufferSize) != -1) {
                            imageWriter.write(bufferSize, 0, bufferSize.length);
                        }
                        Log.e(TAG, "Download completed");
                        break;
                    default:
                        //TODO(hth) log error
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
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
            return 1l;
        }

        protected void onPostExecute(Long result) {
            Log.e(TAG, "Download");
            responseHandler.onSuccess(file, null);
        }
    }

    public void fetchFile(URL url) {
        new DownloadFilesTask().execute(url);
    }

    public String findFileExtension(String contentType) {
        if(contentType.equalsIgnoreCase("image/jpeg")) {
            return ".jpg";
        }

        if(contentType.equalsIgnoreCase("image/png")) {
            return ".png";
        }

        if(contentType.equalsIgnoreCase("image/gif")) {
            return ".gif";
        }

        return ".jpg";
    }
}
