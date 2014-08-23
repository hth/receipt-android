package com.receiptofi.android.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.receiptofi.android.utils.UserUtils;

public final class HTTPUtils {

    public static String HTTP_METHOD_POST = "POST";
    public static String HTTP_METHOD_GET = "GET";

    private static final String CONNECTION_URL_STAGING = "https://receiptofi.com:9443/receipt-mobile";
    private static final String CONNECTION_URL_PRODUCTION = "https://receiptofi.com/receipt-mobile";

    private static final String URL = CONNECTION_URL_STAGING;

    public static String getPostResponse(ArrayList<NameValuePair> params)
            throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);

        httpPost.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(httpPost);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        response.getEntity().getContent()
                )
        );
        StringBuffer responseBuffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuffer.append(line);
        }

        return responseBuffer.toString();
    }

    public static String getResponse(ArrayList<NameValuePair> params)
            throws Exception {
        StringBuilder getParamString = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        if (params != null) {

            for (NameValuePair key : params) {
                getParamString.append("&").append(key.getName() + "=")
                        .append(key.getValue());
            }
        }
        HttpGet httpGet = new HttpGet(URL + getParamString.toString());
        HttpResponse response = client.execute(httpGet);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));
        StringBuffer responseBuffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuffer.append(line);
        }

        return responseBuffer.toString();

    }

    public static void AsyncRequest(ArrayList<NameValuePair> params,
                                    String HTTP_method, ResponseHandler handler) {
        String response = null;
        try {
            if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_POST)) {
                response = getPostResponse(params);
            } else if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_GET)) {
                response = getResponse(params);
            }
            handler.onSuccess(response);
        } catch (Exception e) {
            handler.onExeption(e);
        }

    }

	public static Header[] getHTTPheaders(
			final ArrayList<NameValuePair> params, String API) throws Exception {

		final Header[] headers;
		HttpPost httpPost;
		HttpClient client = new DefaultHttpClient();
		if (API != null) {
			httpPost = new HttpPost(URL + API);
		} else {
			httpPost = new HttpPost(URL);
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
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		headers = response.getAllHeaders();

		return headers;
	}

	public static String uploadImage( String api,String filename) throws Exception {
		
		HttpPost post;
		HttpResponse response;

		HttpClient client = new DefaultHttpClient();
		if (api != null) {
			post = new HttpPost(URL + api);
		} else {
			post = new HttpPost(URL);
		}

		File imageFile = new File(filename);
		FileBody fileBody = new FileBody(imageFile);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("qqfile", fileBody);

		HttpEntity entity = builder.build();

		post.addHeader(API.key.XR_AUTH, UserUtils.getAuth());
		post.addHeader(API.key.XR_MAIL, UserUtils.getEmail());
		post.setEntity(entity);

		response = client.execute(post);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));
		StringBuffer responseBuffer = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			responseBuffer.append(line);
		}

		return responseBuffer.toString();

	}

}
