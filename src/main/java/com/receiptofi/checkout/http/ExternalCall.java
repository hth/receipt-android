package com.receiptofi.checkout.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.UserUtils;

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

    public static void doPost(
            final JSONObject postData,
            final String api,
            final IncludeAuthentication includeAuthentication,
            final ResponseHandler responseHandler
    ) {
        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = getHttpPost(api);
                    StringEntity postEntity = new StringEntity(postData.toString(), "UTF-8");

                    httpPost.setEntity(postEntity);
                    Log.i(TAG, "post=" + httpPost.getURI() + ", data=" + postData.toString());

                    httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
                    if (includeAuthentication == IncludeAuthentication.YES) {
                        httpPost.setHeader(API.key.XR_AUTH, UserUtils.getAuth());
                        httpPost.setHeader(API.key.XR_MAIL, UserUtils.getEmail());
                    }
                    HttpResponse response = new DefaultHttpClient().execute(httpPost);
                    int statusCode = response.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "post, statusCode=" + statusCode + ", body=" + body);
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
     * @param params credential
     * @param responseHandler
     */
    public static void authenticate(
            final List<NameValuePair> params,
            final ResponseHandler responseHandler
    ) {
        new Thread() {
            public void run() {
                try {
                    HttpPost httpPost = getHttpPost(API.LOGIN_API);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    Log.i(TAG, "post=" + httpPost.getURI() + ", login params=*****");
                    HttpResponse response = new DefaultHttpClient().execute(httpPost);

                    int statusCode = response.getStatusLine().getStatusCode();
                    String body = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "post, statusCode=" + statusCode + ", body=" + body);
                    updateResponseHandler(statusCode, response, body, responseHandler);
                } catch (Exception e) {
                    Log.e(TAG, "Fail reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(e);
                }
            }
        }.start();
    }

    public static void doGet(String api, ResponseHandler responseHandler) {
        doGet(IncludeDevice.NO, api, responseHandler);
    }

    public static void doGet(
            final IncludeDevice includeDevice,
            final String api,
            final ResponseHandler responseHandler
    ) {
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
                    Log.i(TAG, "statusCode is:  " + statusCode);
                    String body = EntityUtils.toString(response.getEntity());
                    Log.i(TAG, "body is:  " + body);
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
            Log.i(TAG, "statusCode is:  " + statusCode + "  calling onError");
            responseHandler.onError(statusCode, null);
        } else {
            if (!bodyContainsError(body)) {
                Log.i(TAG, "statusCode is:  " + statusCode + "  body is:  " + body + "  calling onSuccess");
                responseHandler.onSuccess(response.getAllHeaders(), body);
            } else {
                Log.i(TAG, "statusCode is:  " + statusCode + "  body is:  " + body + "  calling onError");
                responseHandler.onError(statusCode, body);
            }
        }
    }

    private static HttpGet getHttpGet(String api) {
        HttpGet httpGet;
        if (!TextUtils.isEmpty(api)) {
            httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + api);
        } else {
            httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
        }
        return httpGet;
    }

    private static HttpPost getHttpPost(String api) {
        HttpPost httpPost;
        if (!TextUtils.isEmpty(api)) {
            httpPost = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + api);
        } else {
            httpPost = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
        }
        return httpPost;
    }

    public static String getPostResponse(
            List<NameValuePair> params,
            String API
    ) throws Exception {

        HttpPost httpPost;
        if (API != null) {
            httpPost = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + API);
        } else {
            httpPost = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
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

    public static String getResponse(List<NameValuePair> params, String API) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;

        if (API != null) {
            httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + API);
        } else {
            httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
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
            final List<NameValuePair> params,
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
                    if (httpMethod.equalsIgnoreCase(Protocol.POST.name())) {
                        response = getPostResponse(params, api);
                    } else if (httpMethod.equalsIgnoreCase(Protocol.GET.name())) {
                        response = getResponse(params, api);
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

                    if (api != null) {
                        httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + api);
                    } else {
                        httpGet = new HttpGet(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
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
                        post = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL + api);
                    } else {
                        post = new HttpPost(MobileServerEndpoints.RECEIPTOFI_MOBILE_URL);
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

    public static Map<String, String> parseHeader(Header[] headers, Set<String> keys) {
        Log.d(TAG, "executing parseHeader");
        if (headers != null && headers.length > 0
                && keys != null && keys.size() > 0) {
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
