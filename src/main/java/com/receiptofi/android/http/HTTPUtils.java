package com.receiptofi.android.http;

import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.models.ImageModel;
import com.receiptofi.android.utils.StringUtil;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class HTTPUtils {

    private static final String TAG = "SUMAN"; //HTTPUtils.class.getSimpleName();

    public static String HTTP_METHOD_POST = "POST";
    public static String HTTP_METHOD_GET = "GET";

    private static final String CONNECTION_URL_LOCAL = "http://192.168.0.150:9090/receipt-mobile";
    //private static final String CONNECTION_URL_STAGING = "https://test.receiptofi.com/receipt-mobile";
    private static final String CONNECTION_URL_STAGING = CONNECTION_URL_LOCAL;
    private static final String CONNECTION_URL_PRODUCTION = "https://live.receiptofi.com/receipt-mobile";

    private static final String RECEIPTOFI_MOBILE_URL = CONNECTION_URL_STAGING;

    public static String getPostResponse(ArrayList<NameValuePair> params, String API)
            throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost;

        if (API != null) {
            httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL + API);
        } else {
            httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(httpPost);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        response.getEntity().getContent()
                )
        );
        StringBuilder responseBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuffer.append(line);
        }

        return responseBuffer.toString();
    }

    public static String getResponse(ArrayList<NameValuePair> params, String API) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;

        if (API != null) {
            httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL + API);
        } else {
            httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL);
        }
        if (params != null) {
            for (NameValuePair pair : params) {
                httpGet.addHeader(pair.getName(), pair.getValue());
            }
        }

        HttpResponse response = client.execute(httpGet);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder responseBuffer = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuffer.append(line);
        }

        return responseBuffer.toString();
    }

    public static void AsyncRequest(
            final ArrayList<NameValuePair> params,
            final String api,
            final String httpMethod,
            final ResponseHandler handler
    ) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                String response = null;
                try {
                    if (httpMethod.equalsIgnoreCase(HTTP_METHOD_POST)) {
                        response = getPostResponse(params, api);
                    } else if (httpMethod.equalsIgnoreCase(HTTP_METHOD_GET)) {
                        response = getResponse(params, api);
                    }
                    handler.onSuccess(null);
                } catch (Exception e) {
                    handler.onException(e);
                }

            }
        }.start();
    }

    public static Header[] getHTTPHeaders(
            final ArrayList<NameValuePair> params,
            String API
    ) throws Exception {

        final Header[] headers;
        HttpPost httpPost;
        HttpClient client = new DefaultHttpClient();
        if (API != null) {
            httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL + API);
        } else {
            httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            response = client.execute(httpPost);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        headers = response.getAllHeaders();

        return headers;
    }

    public static void doSocialAuthentication(
            final Context ctx,
            final JSONObject postData,
            final String API,
            final ResponseHandler responseHandler
    ) {
        new Thread() {
            public void run() {
                try {
                    final Header[] headers;
                    HttpPost httpPost;
                    HttpClient client = new DefaultHttpClient();
                    StringBuilder responseBuffer = new StringBuilder();

                    if (API != null) {
                        httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL + API);
                    } else {
                        httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
                    }
                    Log.i("making api request to server", RECEIPTOFI_MOBILE_URL + API + ", Data: " + postData.toString());

                    StringEntity postEntity = new StringEntity(postData.toString());

                    httpPost.setEntity(postEntity);
                    httpPost.setHeader("Content-Type", "application/json");
                    HttpResponse response;

                    response = client.execute(httpPost);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuffer.append(line);
                    }
                    headers = response.getAllHeaders();
                    Log.i("Response", responseBuffer.toString());
                    if (isValidSocialAuthResponse(ctx, headers)) {
                        responseHandler.onSuccess(headers);
                    } else {
                        responseHandler.onError(response.getStatusLine().getStatusCode(), responseBuffer.toString());
                    }

                } catch (Exception e) {
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    private static boolean isValidSocialAuthResponse(Context ctx, Header[] headers) {
        if (headers != null && headers.length >= 2) {
            ArrayList<String> headerList = new ArrayList<String>();

            for (Header header : headers) {
                String key = header.getName();
                if (key != null && (key.trim().equals(API.key.XR_MAIL) || key.trim().equals(API.key.XR_AUTH))) {
                    headerList.add(key);
                    KeyValue.insertKeyValue(ctx, key, header.getValue());
                }
            }
            if (headerList.contains(API.key.XR_MAIL) && headerList.contains(API.key.XR_AUTH)) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public static void doPost(
            final JSONObject postData,
            final String API,
            final ResponseHandler responseHandler
    ) {
        new Thread() {
            public void run() {
                try {
                    Log.d(TAG, "executing doPost");
                    HttpPost httpPost;
                    HttpClient client = new DefaultHttpClient();

                    if (API != null) {
                        httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL + API);
                    } else {
                        httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
                    }
                    Log.d(TAG, "making api request to server: " + RECEIPTOFI_MOBILE_URL + API + ", Data: " + postData.toString());

                    StringEntity postEntity = new StringEntity(postData.toString(), "UTF-8");
                    //StringEntity postEntity = new StringEntity(postData.toString());

                    httpPost.setEntity(postEntity);
                    httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
                    HttpResponse response;

                    response = client.execute(httpPost);

                    int statusCode = response.getStatusLine().getStatusCode();
                    Log.i(TAG, "statusCode is:  " + statusCode);
                    String body = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "body is:  " + body);
                    if (statusCode != 200) {
                        Log.i(TAG, "statusCode is:  " + statusCode + "  calling onError");
                        responseHandler.onError(statusCode, null);
                        return;
                    }
                    // if(StringUtil.isEmpty(body)){
                    if (!bodyContainsError(body)) {
                        Log.i(TAG, "statusCode is:  " + statusCode + "  body is:  " + body + "  calling onSuccess");
                        responseHandler.onSuccess(response.getAllHeaders());
                        return;
                    } else {
                        Log.i(TAG, "statusCode is:  " + statusCode + "  body is:  " + body + "  calling onError");
                        responseHandler.onError(statusCode, body);
                        return;
                    }
                } catch (Exception e) {
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    public static void downloadImage(
            Context ctx,
            final File imageFile,
            final String api,
            final ResponseHandler responseHandler) {
        new Thread() {
            public void run() {
                try {
                    HttpResponse response;
                    HttpClient client = new DefaultHttpClient();

                    HttpGet httpGet;

                    if (api != null) {
                        httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL + api);
                    } else {
                        httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL);
                    }

                    httpGet.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                    httpGet.addHeader(API.key.XR_MAIL, UserUtils.getEmail());

                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();

                    BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
                    int inByte;
                    while ((inByte = bis.read()) != -1) bos.write(inByte);
                    bis.close();
                    bos.close();

                    responseHandler.onSuccess(null);
                } catch (Exception e) {
                    responseHandler.onException(e);
                }

            }
        }.start();
    }

    public static Thread uploadImage(
            final Context context,
            final String api,
            final ImageModel imageModel,
            final ImageResponseHandler handler
    ) {
        Thread t = new Thread() {
            public void run() {
                try {
                    HttpPost post;
                    HttpResponse response;

                    HttpClient client = new DefaultHttpClient();
                    if (api != null) {
                        post = new HttpPost(RECEIPTOFI_MOBILE_URL + api);
                    } else {
                        post = new HttpPost(RECEIPTOFI_MOBILE_URL);
                    }

                    File imageFile = new File(imageModel.imgPath);
                    FileBody fileBody = new FileBody(imageFile);

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    builder.addPart("qqfile", fileBody);

                    HttpEntity entity = builder.build();

                    post.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                    post.addHeader(API.key.XR_MAIL, UserUtils.getEmail());
                    post.setEntity(entity);

                    response = client.execute(post);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder responseBuffer = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuffer.append(line);
                    }

                    handler.onSuccess(imageModel, responseBuffer.toString());
                } catch (Exception e) {
                    // TODO: handle exception
                    handler.onException(imageModel, e);
                }
            }
        };

        t.start();
        return t;
    }


    public static boolean bodyContainsError(String body) {
        return !StringUtil.isEmpty(body) && body.contains("error");
    }

    public static Map<String, String> parseHeader(Header[] headers, Set<String> keys) {
        Log.d(TAG, "executing parseHeader");
        if (headers != null && headers.length > 0
                && keys != null && keys.size() > 0) {
            Map<String, String> headerData = new HashMap<String, String>();

            for (Header header : headers) {
                String key = header.getName();
                if (!StringUtil.isEmpty(key) && (keys.contains(key))) {
                    headerData.put(key, header.getValue());
                    Log.d(TAG, "Fetching header data: key is:  " + key + "  value is:  " + header.getValue());
                }
            }
            Log.d(TAG, "headerData is: " + headerData);
            return headerData;
        }
        Log.d(TAG, "Couldn't parse header");
        return null;
    }

    /**
     * Get content type from file extension.
     * Note: Do not set the content type when uploading file to server as it will stop uploading.
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        Log.d("Content-Type", type);
        return type;
    }
}
