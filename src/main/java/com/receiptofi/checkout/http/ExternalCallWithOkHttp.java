package com.receiptofi.checkout.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.checkout.BuildConfig;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.model.ImageModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.model.types.IncludeDevice;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: hitender
 * Date: 6/7/15 2:57 AM
 */
public class ExternalCallWithOkHttp {
    private static final String TAG = ExternalCallWithOkHttp.class.getSimpleName();
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=UTF-8");
    public static final MediaType MEDIA_TYPE_FORM = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");

    private ExternalCallWithOkHttp() {
    }

    public static void doPost(
            Context context,
            JSONObject postData,
            String api,
            IncludeAuthentication includeAuthentication,
            ResponseHandler responseHandler
    ) {
        doPost(context, postData, api, includeAuthentication, IncludeDevice.NO, responseHandler);
    }

    public static void doPost(
            Context context,
            String api,
            IncludeAuthentication includeAuthentication,
            IncludeDevice includeDevice,
            ResponseHandler responseHandler
    ) {
        doPost(context, null, api, includeAuthentication, includeDevice, responseHandler);
    }

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
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    if (includeAuthentication == IncludeAuthentication.YES) {
                        headers.put(API.key.XR_AUTH, UserUtils.getAuth());
                        headers.put(API.key.XR_MAIL, UserUtils.getEmail());
                    }
                    if (includeDevice == IncludeDevice.YES) {
                        headers.put(API.key.XR_DID, UserUtils.getDeviceId());
                    }

                    Request request;
                    if (!TextUtils.isEmpty(api)) {
                        request = new Request.Builder()
                                .url(BuildConfig.RECEIPTOFI_MOBILE + api)
                                .headers(Headers.of(headers))
                                .post(getRequestBody(MEDIA_TYPE_JSON, postData))
                                .build();
                    } else {
                        request = new Request.Builder()
                                .url(BuildConfig.RECEIPTOFI_MOBILE)
                                .headers(Headers.of(headers))
                                .post(getRequestBody(MEDIA_TYPE_JSON, postData))
                                .build();
                    }

                    Log.i(TAG, "post=" + request.httpUrl());
                    Response response = new OkHttpClient().newCall(request).execute();
                    int statusCode = response.code();
                    String body = response.body().string();
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
     */
    public static void authenticate(
            final Context context,
            final RequestBody formBody,
            final ResponseHandler responseHandler
    ) {
        if (!AppUtils.isNetworkConnected(context)) {
            responseHandler.onException(new Exception(context.getString(R.string.no_network_available)));
            return;
        }
        new Thread() {
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(BuildConfig.RECEIPTOFI_MOBILE + API.LOGIN_API)
                            .post(formBody)
                            .build();

                    Log.i(TAG, "post=" + request.httpUrl() + ", login params=*****");
                    Response response = new OkHttpClient().newCall(request).execute();

                    int statusCode = response.code();
                    String body = response.body().string();
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

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json;charset=UTF-8");
                    headers.put(API.key.XR_AUTH, UserUtils.getAuth());
                    headers.put(API.key.XR_MAIL, UserUtils.getEmail());
                    if (includeDevice == IncludeDevice.YES) {
                        headers.put(API.key.XR_DID, UserUtils.getDeviceId());
                    }

                    Request request;
                    if (!TextUtils.isEmpty(api)) {
                        request = new Request.Builder()
                                .url(BuildConfig.RECEIPTOFI_MOBILE + api)
                                .headers(Headers.of(headers))
                                .build();
                    } else {
                        request = new Request.Builder()
                                .url(BuildConfig.RECEIPTOFI_MOBILE)
                                .headers(Headers.of(headers))
                                .build();
                    }

                    Response response = new OkHttpClient().newCall(request).execute();
                    int statusCode = response.code();
                    String body = response.body().string();
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
            Response response,
            String body,
            ResponseHandler responseHandler
    ) {
        if (statusCode != 200) {
            Log.i(TAG, "statusCode=" + statusCode + " onError");
            responseHandler.onError(statusCode, null);
        } else {
            if (!bodyContainsError(body)) {
                Log.i(TAG, "statusCode=" + statusCode + ", body=" + body + " onSuccess");
                responseHandler.onSuccess(response.headers(), body);
            } else {
                Log.i(TAG, "statusCode=" + statusCode + ", body=" + body + " onError");
                responseHandler.onError(statusCode, body);
            }
        }
    }

    public static boolean bodyContainsError(String body) {
        return !TextUtils.isEmpty(body) && body.contains("error");
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
                    RequestBody requestBody = new MultipartBuilder()
                            .type(MultipartBuilder.FORM)
                            .addPart(
                                    Headers.of("Content-Disposition", "form-data; name=\"qqfile\""),
                                    RequestBody.create(
                                            MediaType.parse(imageModel.imgPath),
                                            new File(imageModel.imgPath)))
                            .build();

                    Request request = new Request.Builder()
                            .url(BuildConfig.RECEIPTOFI_MOBILE + api)
                            .post(requestBody)
                            .addHeader(API.key.XR_AUTH, UserUtils.getAuth())
                            .addHeader(API.key.XR_MAIL, UserUtils.getEmail())
                            .build();

                    Response response = new OkHttpClient().newCall(request).execute();
                    handler.onSuccess(imageModel, response.body().string());
                } catch (Exception e) {
                    // TODO: handle exception
                    handler.onException(imageModel, e);
                }
            }
        };

        t.start();
        return t;
    }

    private static RequestBody getRequestBody(MediaType mediaType, JSONObject postData) {
        RequestBody requestBody;
        if (null != postData) {
            Log.i(TAG, "postData=" + postData.toString());
            requestBody = RequestBody.create(mediaType, postData.toString());
        } else {
            Log.i(TAG, "postData is null");
            requestBody = getRequestBodyEmpty(mediaType);
        }
        return requestBody;
    }

    private static RequestBody getRequestBodyEmpty(MediaType mediaType) {
        return RequestBody.create(mediaType, "{}");
    }

    public static Map<String, String> parseHeader(Headers headers, Set<String> keys) {
        Log.d(TAG, "executing parseHeader");
        if (headers != null && headers.size() > 0 && keys != null && !keys.isEmpty()) {
            Map<String, String> headerData = new HashMap<>();

            for (String name : headers.names()) {
                if (!TextUtils.isEmpty(name) && (keys.contains(name))) {
                    headerData.put(name, headers.get(name));
                    Log.d(TAG, "Fetching header data: key is:  " + name + "  value is:  " + headers.get(name));
                }
            }
            Log.d(TAG, "headerData is: " + headerData);
            return headerData;
        }
        Log.d(TAG, "Couldn't parse header");
        return null;
    }
}
