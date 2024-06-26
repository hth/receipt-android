package com.receiptofi.receiptapp.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.receiptapp.BuildConfig;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.model.ImageModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.model.types.IncludeDevice;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.UserUtils;

import org.json.JSONObject;

import java.io.File;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static okhttp3.OkHttpClient.*;

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
        if (!AppUtils.isNetworkConnectedOrConnecting(context)) {
            responseHandler.onException(new RuntimeException(context.getString(R.string.no_network_available)));
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
                    if (includeDevice == IncludeDevice.TYPE) {
                        headers.put(API.key.XR_DID, UserUtils.getDeviceId());
                        headers.put(API.key.XR_TK, UserUtils.getToken());
                        headers.put(API.key.XR_DT, "A");
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

                    Log.d(TAG, "post=" + request.url().toString());
                    Response response = okHttpClient(3).newCall(request).execute();
                    updateResponseHandler(response.code(), response, response.body().string(), responseHandler);
                } catch (ConnectException e) {
                    Log.e(TAG, "api=" + api + " reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.connect_to_server_failure)));
                } catch (Exception e) {
                    Log.e(TAG, "api=" + api + " reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.post_get_general_error)));
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
        if (!AppUtils.isNetworkConnectedOrConnecting(context)) {
            responseHandler.onException(new RuntimeException(context.getString(R.string.no_network_available)));
            return;
        }
        new Thread() {
            public void run() {
                try {
                    Request request = new Request.Builder()
                            .url(BuildConfig.RECEIPTOFI_MOBILE + API.LOGIN_API)
                            .post(formBody)
                            .build();

                    Log.i(TAG, "post=" + request.url().toString() + ", login params=*****");
                    Response response = okHttpClient(3).newCall(request).execute();
                    updateResponseHandler(response.code(), response, response.body().string(), responseHandler);
                } catch (ConnectException e) {
                    Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.connect_to_server_failure)));
                } catch (Exception e) {
                    Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.post_get_general_error)));
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
        if (!AppUtils.isNetworkConnectedOrConnecting(context)) {
            responseHandler.onException(new RuntimeException(context.getString(R.string.no_network_available)));
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
                    if (includeDevice == IncludeDevice.TYPE) {
                        headers.put(API.key.XR_DID, UserUtils.getDeviceId());
                        headers.put(API.key.XR_TK, UserUtils.getToken());
                        headers.put(API.key.XR_DT, "A");
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

                    Log.d(TAG, "get=" + request.url().toString());
                    Response response = okHttpClient(5).newCall(request).execute();
                    updateResponseHandler(response.code(), response, response.body().string(), responseHandler);
                } catch (ConnectException e) {
                    Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.connect_to_server_failure)));
                } catch (Exception e) {
                    Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                    responseHandler.onException(new RuntimeException(context.getString(R.string.post_get_general_error)));
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
            Log.i(TAG, response.request().method() + " statusCode=" + statusCode + " onError");
            if (statusCode == 404) {
                responseHandler.onError(statusCode, response.message() + ". Please re-sync with server.");
            } else {
                Log.e(TAG, "Missing JSON representation");
                responseHandler.onError(statusCode, response.message());
            }
        } else {
            if (!bodyContainsError(body)) {
                if (body.isEmpty()) {
                    Log.d(TAG, response.request().method() + " statusCode=" + statusCode + ", body=EMPTY onSuccess");
                } else {
                    Log.d(TAG, response.request().method() + " statusCode=" + statusCode + ", body=" + body + " onSuccess");
                }
                responseHandler.onSuccess(response.headers(), body);
            } else {
                Log.i(TAG, response.request().method() + " statusCode=" + statusCode + ", body=" + body + " onError");
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
                    File file = new File(imageModel.imgPath);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart(
                                    "qqfile",
                                    file.getName(),
                                    RequestBody.create(MediaType.parse("media/type"), file))
                            .build();

                    Request request = new Request.Builder()
                            .url(BuildConfig.RECEIPTOFI_MOBILE + api)
                            .post(requestBody)
                            .addHeader(API.key.XR_AUTH, UserUtils.getAuth())
                            .addHeader(API.key.XR_MAIL, UserUtils.getEmail())
                            .build();

                    Log.d(TAG, "uploadImage=" + request.url().toString());
                    Response response = okHttpClient(10).newCall(request).execute();
                    handler.onSuccess(imageModel, response.body().string());
                } catch (UnknownHostException e) {
                    Log.e(TAG, "failed connection reason=" + e.getLocalizedMessage(), e);
                    handler.onException(imageModel, new RuntimeException(context.getString(R.string.post_image_upload_error)));
                } catch (Exception e) {
                    Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
                    handler.onException(imageModel, new RuntimeException(context.getString(R.string.post_image_upload_error)));
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

    private static OkHttpClient okHttpClient(int timeInMinutes) {
        Builder builder = new Builder();
        builder.readTimeout(timeInMinutes, TimeUnit.MINUTES);
        return builder.build();
    }
}
