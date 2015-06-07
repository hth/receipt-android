package com.receiptofi.checkout.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.receiptofi.checkout.BuildConfig;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.types.Protocol;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.squareup.okhttp.Headers;

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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ExternalCall {

    private static final String TAG = ExternalCall.class.getSimpleName();

    private ExternalCall() {
    }

    @Deprecated
    public static void doPost(
            Context context,
            JSONObject postData,
            String api,
            IncludeAuthentication includeAuthentication,
            ResponseHandler responseHandler
    ) {
        doPost(context, postData, api, includeAuthentication, IncludeDevice.NO, responseHandler);
    }

    @Deprecated
    public static void doPost(
            Context context,
            String api,
            IncludeAuthentication includeAuthentication,
            IncludeDevice includeDevice,
            ResponseHandler responseHandler
    ) {
        doPost(context, null, api, includeAuthentication, includeDevice, responseHandler);
    }

    @Deprecated
    public static void doPost(
            final Context context,
            final JSONObject postData,
            final String api,
            final IncludeAuthentication includeAuthentication,
            final IncludeDevice includeDevice,
            final ResponseHandler responseHandler
    ) {
        if (!AppUtils.isNetworkConnected(context)) {
            responseHandler.onException(new Exception(context.getString(R.string.no_network_available)));
            return;
        }
        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = getHttpPost(api);

                    Log.i(TAG, "post=" + httpPost.getURI());
                    if (null != postData) {
                        Log.i(TAG, "data=" + postData.toString());
                        StringEntity postEntity = new StringEntity(postData.toString(), "UTF-8");
                        httpPost.setEntity(postEntity);
                    }

                    httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
                    if (includeAuthentication == IncludeAuthentication.YES) {
                        httpPost.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                        httpPost.addHeader(API.key.XR_MAIL, UserUtils.getEmail());
                    }
                    if (includeDevice == IncludeDevice.YES) {
                        httpPost.addHeader(API.key.XR_DID, UserUtils.getDeviceId());
                    }

                    HttpResponse response = new DefaultHttpClient().execute(httpPost);
                    int statusCode = response.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(response.getEntity());
                    if (body.isEmpty()) {
                        Log.i(TAG, "post, statusCode=" + statusCode + ", body=EMPTY");
                    } else {
                        Log.i(TAG, "post, statusCode=" + statusCode + ", body=" + body);
                    }
                    updateResponseHandler(statusCode, response, body, responseHandler);
                } catch (Exception e) {
                    Log.e(TAG, "Fail reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    /**
     * Authenticate user.
     *
     * @param params          credential
     * @param responseHandler
     */
    public static void authenticate(
            final Context context,
            final List<NameValuePair> params,
            final ResponseHandler responseHandler
    ) {
        if (!AppUtils.isNetworkConnected(context)) {
            responseHandler.onException(new Exception(context.getString(R.string.no_network_available)));
            return;
        }
        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = getHttpPost(API.LOGIN_API);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    Log.i(TAG, "post=" + httpPost.getURI() + ", login params=*****");
                    HttpResponse response = new DefaultHttpClient().execute(httpPost);

                    int statusCode = response.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(response.getEntity());
                    if (body.isEmpty()) {
                        Log.i(TAG, "post, statusCode=" + statusCode + ", body=EMPTY");
                    } else {
                        Log.i(TAG, "post, statusCode=" + statusCode + ", body=" + body);
                    }
                    updateResponseHandler(statusCode, response, body, responseHandler);
                } catch (Exception e) {
                    Log.e(TAG, "Fail reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    public static void doGet(Context context, String api, ResponseHandler responseHandler) {
        doGet(context, IncludeDevice.NO, api, responseHandler);
    }

    public static void doGet(
            final Context context,
            final IncludeDevice includeDevice,
            final String api,
            final ResponseHandler responseHandler
    ) {
        if (!AppUtils.isNetworkConnected(context)) {
            responseHandler.onException(new Exception(context.getString(R.string.no_network_available)));
            return;
        }
        new Thread() {
            public void run() {
                try {
                    HttpGet httpGet = getHttpGet(api);
                    httpGet.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                    httpGet.addHeader(API.key.XR_MAIL, UserUtils.getEmail());
                    if (includeDevice == IncludeDevice.YES) {
                        httpGet.addHeader(API.key.XR_DID, UserUtils.getDeviceId());
                    }

                    HttpResponse response = new DefaultHttpClient().execute(httpGet);
                    int statusCode = response.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(response.getEntity());
                    if (body.isEmpty()) {
                        Log.i(TAG, "get, statusCode=" + statusCode + ", body=EMPTY");
                    } else {
                        Log.i(TAG, "get, statusCode=" + statusCode + ", body=" + body);
                    }
                    updateResponseHandler(statusCode, response, body, responseHandler);
                } catch (Exception e) {
                    Log.e(TAG, "Fail reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    private static void updateResponseHandler(
            int statusCode,
            HttpResponse response,
            String body,
            ResponseHandler responseHandler
    ) {
        if (statusCode != 200) {
            Log.i(TAG, "statusCode=" + statusCode + " onError");
            responseHandler.onError(statusCode, null);
        } else {
            if (!bodyContainsError(body)) {
                Log.i(TAG, "statusCode=" + statusCode + ", body=" + body + " onSuccess");

                Map<String, String> headerMap = new HashMap<>();
                Header[] headers = response.getAllHeaders();
                for(Header header : headers) {
                    headerMap.put(header.getName(), header.getValue());
                }

                responseHandler.onSuccess(Headers.of(headerMap), body);
            } else {
                Log.i(TAG, "statusCode=" + statusCode + ", body=" + body + " onError");
                responseHandler.onError(statusCode, body);
            }
        }
    }

    private static HttpGet getHttpGet(String api) {
        if (!TextUtils.isEmpty(api)) {
            return new HttpGet(BuildConfig.RECEIPTOFI_MOBILE + api);
        } else {
            return new HttpGet(BuildConfig.RECEIPTOFI_MOBILE);
        }
    }

    private static HttpPost getHttpPost(String api) {
        if (!TextUtils.isEmpty(api)) {
            return new HttpPost(BuildConfig.RECEIPTOFI_MOBILE + api);
        } else {
            return new HttpPost(BuildConfig.RECEIPTOFI_MOBILE);
        }
    }

    public static String getPostResponse(
            Context context,
            List<NameValuePair> params,
            String API
    ) throws Exception {

        HttpPost httpPost;
        if (API != null) {
            httpPost = new HttpPost(BuildConfig.RECEIPTOFI_MOBILE + API);
        } else {
            httpPost = new HttpPost(BuildConfig.RECEIPTOFI_MOBILE);
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = new DefaultHttpClient().execute(httpPost);

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

    public static String getResponse(Context context, List<NameValuePair> params, String API) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;

        if (API != null) {
            httpGet = new HttpGet(BuildConfig.RECEIPTOFI_MOBILE + API);
        } else {
            httpGet = new HttpGet(BuildConfig.RECEIPTOFI_MOBILE);
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
            final Context context,
            final List<NameValuePair> params,
            final String api,
            final String httpMethod,
            final ResponseHandler handler
    ) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String response = null;
                try {
                    if (httpMethod.equalsIgnoreCase(Protocol.POST.name())) {
                        response = getPostResponse(context, params, api);
                    } else if (httpMethod.equalsIgnoreCase(Protocol.GET.name())) {
                        response = getResponse(context, params, api);
                    }
                    handler.onSuccess(null, null);
                } catch (Exception e) {
                    handler.onException(e);
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

                    if (null != api) {
                        httpGet = new HttpGet(BuildConfig.RECEIPTOFI_MOBILE + api);
                    } else {
                        httpGet = new HttpGet(BuildConfig.RECEIPTOFI_MOBILE);
                    }

                    httpGet.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
                    httpGet.addHeader(API.key.XR_MAIL, UserUtils.getEmail());

                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();

                    BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
                    int inByte;
                    while ((inByte = bis.read()) != -1) {
                        bos.write(inByte);
                    }
                    bis.close();
                    bos.close();

                    responseHandler.onSuccess(null, null);
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
                        post = new HttpPost(BuildConfig.RECEIPTOFI_MOBILE + api);
                    } else {
                        post = new HttpPost(BuildConfig.RECEIPTOFI_MOBILE);
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
        return !TextUtils.isEmpty(body) && body.contains("error");
    }

    @Deprecated
    public static Map<String, String> parseHeader(Header[] headers, Set<String> keys) {
        Log.d(TAG, "executing parseHeader");
        if (headers != null && headers.length > 0 && keys != null && !keys.isEmpty()) {
            Map<String, String> headerData = new HashMap<>();

            for (Header header : headers) {
                String key = header.getName();
                if (!TextUtils.isEmpty(key) && (keys.contains(key))) {
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

    public static Map<String, String> parseHeader(Headers headers, Set<String> keys) {
        Log.d(TAG, "executing parseHeader");
        if (headers != null && headers.size() > 0 && keys != null && !keys.isEmpty()) {
            Map<String, String> headerData = new HashMap<>();

            for (String name : headers.names()) {
                headerData.put(name, headers.get(name));
                Log.d(TAG, "Fetching header data: key is:  " + name + "  value is:  " + headers.get(name));
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
