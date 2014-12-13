package com.receiptofi.android.http;

public class API {
    public static final String LOGIN_API = "/j_spring_security_check";
    public static final String SIGNUP_API = "/registration.json";
    public static final String PASSWORD_RECOVER_API = "/recover.json";
    public static final String UPLOAD_IMAGE_API = "/api/upload.json";
    public static final String GET_ALL_RECEIPTS = "/api/allReceipts.json";
    public static final String SOCIAL_LOGIN_API = "/authenticate.json";
    public static final String VIEW_RECEIPT_DETAIL = "/api/receiptDetail/";
    public static final String DOWNLOAD_IMAGE = "/api/image/";

    public static class key {
        // Header
        public static final String XR_MAIL = "X-R-MAIL";
        public static final String XR_AUTH = "X-R-AUTH";

        // Social
        public static final String PID = "pid";
        public static final String ACCESS_TOKEN = "at";
        public static final String PID_FACEBOOK = "FACEBOOK";
        public static final String PID_GOOGLE = "GOOGLE";

        // Server client id
        public static final String SERVER_CLIENT_ID_LOCAL = "917069462497-aa1hduf9dh69niv19aq57hp422vqc5p1.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID_TEST = "917069462497-8adldf383r8d5g5cdj867dpuus444i49.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID_LIVE = "917069462497-62ejpasmc8ptjpujgi207fqsldh2lma3.apps.googleusercontent.com";
        public static final String SERVER_CLIENT_ID = SERVER_CLIENT_ID_LOCAL;

        // Sign in
        public static final String SIGNIN_EMAIL = "mail";
        public static final String SIGNIN_PASSWORD = "password";

        // Sign up
        public static final String SIGNUP_EMAIL = "EM";
        public static final String SIGNUP_FIRSTNAME = "FN";
        public static final String SIGNUP_PASSWORD = "PW";
        public static final String SIGNUP_AGE = "BD";

        // Password recovery
        public static final String PASSWORD_RECOVERY_EMAIL = "EM";

    }
}
