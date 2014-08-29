package com.receiptofi.android.http;

public class API {
	public static final String LOGIN_API = "/j_spring_security_check";
	public static final String UPLOAD_IMAGE_API ="/api/upload.json";
	public static final String GET_ALL_RECEIPTS ="/api/allReceipts.json";
	
	public static class key {
		public static final String XR_MAIL = "X-R-MAIL";
		public static final String XR_AUTH = "X-R-AUTH";
	}
}
