package com.receiptofi.android.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.receiptofi.android.db.KeyValue;
import com.receiptofi.android.models.ImageModel;
import com.receiptofi.android.services.ImageDownloadService;
import com.receiptofi.android.utils.UserUtils;

public final class HTTPUtils {
    public static String HTTP_METHOD_POST = "POST";
    public static String HTTP_METHOD_GET = "GET";

//    private static final String CONNECTION_URL_STAGING = "https://receiptofi.com:9443/receipt-mobile";
    private static final String CONNECTION_URL_STAGING = "https://test.receiptofi.com/receipt-mobile";
    private static final String CONNECTION_URL_PRODUCTION = "https://live.receiptofi.com/receipt-mobile";

    private static final String RECEIPTOFI_MOBILE_URL = CONNECTION_URL_STAGING;

    public static String getPostResponse(ArrayList<NameValuePair> params,String API)
            throws Exception {

        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost ;

        if(API!=null){
        	httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL +API);
        }else {
        	httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
		}

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

    public static String getResponse(ArrayList<NameValuePair> params,String API)
            throws Exception {
        StringBuilder getParamString = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet;
        
        if(API!=null){
        	httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL + API);
        }else {
        	httpGet = new HttpGet(RECEIPTOFI_MOBILE_URL);
		}
        if (params != null) {
        	
        	for(NameValuePair pair:params){
        		 httpGet.addHeader(pair.getName(), pair.getValue());
        	}
        	  
        }

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

    public static void AsyncRequest(final ArrayList<NameValuePair> params,
    		final String API,final String HTTP_method, final ResponseHandler handler) {
    	new Thread(){
    		@Override
    		public void run() {
    			// TODO Auto-generated method stub
    			super.run();
    			   String response = null;
    		        try {
    		            if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_POST)) {
    		                response = getPostResponse(params,API);
    		            } else if (HTTP_method.equalsIgnoreCase(HTTP_METHOD_GET)) {
    		                response = getResponse(params,API);
    		            }
    		            handler.onSuccess(response);
    		        } catch (Exception e) {
    		            handler.onExeption(e);
    		        }

    		}
    	}.start();
     
    }

	public static Header[] getHTTPHeaders(
			final ArrayList<NameValuePair> params, String API) throws Exception {

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

	public static void doSocialAuthentication(final Context ctx,
			final JSONObject postData, final String API,final ResponseHandler responseHandler) {
		
		new Thread() {
			public void run() {
				try {
					final Header[] headers;
					HttpPost httpPost;
					HttpClient client = new DefaultHttpClient();
					StringBuffer responseBuffer = new StringBuffer();
					
					if (API != null) {
						httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL + API);
					} else {
						httpPost = new HttpPost(RECEIPTOFI_MOBILE_URL);
					}
					Log.i("making api request to server", RECEIPTOFI_MOBILE_URL + API + ", Data: " + postData.toString());
					
					StringEntity postEntity = new StringEntity(postData.toString());

					httpPost.setEntity(postEntity);
					httpPost.setHeader("Content-Type", "application/json");
					HttpResponse response = null;

					response = client.execute(httpPost);

					BufferedReader reader = new BufferedReader(new InputStreamReader(
							response.getEntity().getContent()));
					
					String line;
					while ((line = reader.readLine()) != null) {
						responseBuffer.append(line);
					}
					headers = response.getAllHeaders();
				    Log.i("Response", responseBuffer.toString());
					if(isValidSocialAuthResponse(ctx,headers)){
						responseHandler.onSuccess(responseBuffer.toString());
					}else {
						responseHandler.onError(responseBuffer.toString());
					}
					
				} catch (Exception e) {
					responseHandler.onExeption(e);
				}
			};
		}.start();

		
		
	}
	
	private static boolean isValidSocialAuthResponse(Context ctx,Header[] headers) {
		if(headers!=null && headers.length>=2){
			ArrayList<String> headerList = new ArrayList<String>();
			
			for(Header header:headers){
				 String key = header.getName();
                 if (key != null && (key.trim().equals(API.key.XR_MAIL) || key.trim().equals(API.key.XR_AUTH))) {
                	 headerList.add(key);
                	 KeyValue.insertKeyValue(ctx, key, header.getValue());
                 }
			}
			if(headerList.contains(API.key.XR_MAIL) && headerList.contains(API.key.XR_AUTH)){
				return true;
			}else {
				return false;
			}
			
		}else {
			return false;
		}
	

	}

	public static void downloadImage(String blobId, String uri, DownloadImageResponseHandler responseHandler) {
		ImageDownloadService imageDownloadService = new ImageDownloadService(blobId, uri, responseHandler);
		try {
			imageDownloadService.fetchFile(new URL(RECEIPTOFI_MOBILE_URL + uri));
		} catch (MalformedURLException e) {
			responseHandler.onException(e);
		}
	}
	
	public static Thread uploadImage(final Context context,final String api, final ImageModel imageModel, final ImageResponseHandler handler){
		Thread t=
		new Thread(){
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
					StringBuffer responseBuffer = new StringBuffer();
					String line;
					while ((line = reader.readLine()) != null) {
						responseBuffer.append(line);
					}

					handler.onSuccess(imageModel,responseBuffer.toString());
				} catch (Exception e) {
					// TODO: handle exception
					handler.onExeption(imageModel,e);
				}
			};
		};
		t.start();
		return t;
	}
}
