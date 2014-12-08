package com.receiptofi.android.http;

public class API {
    public static final String LOGIN_API = "/j_spring_security_check";
    public static final String UPLOAD_IMAGE_API = "/api/upload.json";
    public static final String GET_ALL_RECEIPTS = "/api/allReceipts.json";
    public static final String SOCIAL_LOGIN_API = "/authenticate.json";
    public static final String VIEW_RECEIPT_DETAIL = "/api/receiptDetail/";
    public static final String DOWNLOAD_IMAGE = "/api/image/";

    public static class key {
        public static final String XR_MAIL = "X-R-MAIL";
        public static final String XR_AUTH = "X-R-AUTH";
        public static final String PID = "pid";
        public static final String ACCESS_TOKEN = "at";
        public static final String AUTH_KEY = "auth";
        public static final String PID_FACEBOOK = "FACEBOOK";
        public static final String PID_GOOGLE = "GOOGLE";
        public static final String EMAIL = "mail";
        public static final String PASSWORD = "password";
        public static final String SERVER_CLIENT_ID_LOCAL = "917069462497-aa1hduf9dh69niv19aq57hp422vqc5p1.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID_TEST = "917069462497-8adldf383r8d5g5cdj867dpuus444i49.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID_LIVE = "917069462497-62ejpasmc8ptjpujgi207fqsldh2lma3.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID = SERVER_CLIENT_ID_LOCAL;
    }
}
