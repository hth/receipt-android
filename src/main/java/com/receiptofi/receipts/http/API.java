package com.receiptofi.receipts.http;

public class API {

    private API() {
    }

    // Account api
    public static final String LOGIN_API = "/j_spring_security_check";
    public static final String SIGNUP_API = "/registration.json";
    public static final String SOCIAL_LOGIN_API = "/authenticate.json";
    public static final String PASSWORD_RECOVER_API = "/recover.json";

    // Launch HomeActivity api
    // On successful login check if XR-mail is same and DID exist skip device registration
    // otherwise register device and call ALL_FROM_BEGINNING
    public static final String REGISTER_DEVICE = "/api/register.json";
    public static final String ALL_FROM_BEGINNING = "/api/all.json";
    // if re-login call NEW_UPDATE_FOR_DEVICE
    public static final String NEW_UPDATE_FOR_DEVICE = "/api/update.json";


    // Settings api - Profile
    public static final String SETTINGS_UPDATE_LOGIN_ID_API = "/api/updateMail.json";
    public static final String SETTINGS_UPDATE_PASSWORD_API = "/api/updatePassword.json";

    // Settings api - Preference
    public static final String ADD_EXPENSE_TAG = "/api/addExpenseTag.json";
    public static final String UPDATE_EXPENSE_TAG = "/api/updateExpenseTag.json";
    public static final String DELETE_EXPENSE_TAG = "/api/deleteExpenseTag.json";

    // Home page api
    public static final String UNPROCESSED_COUNT_API = "/api/unprocessed.json";

    public static final String UPLOAD_IMAGE_API = "/api/upload.json";
    public static final String GET_ALL_RECEIPTS = "/api/allReceipts.json";
    public static final String VIEW_RECEIPT_DETAIL = "/api/receiptDetail/";
    public static final String DOWNLOAD_IMAGE = "/api/image/";
    public static final String RECEIPT_ACTION = "/api/receiptAction.json";

    // Braintree Payment
    public static final String TOKEN_API = "/api/token.json";
    public static final String PLANS_API = "/api/plans.json";
    public static final String PAYMENT_API = "/api/payment.json";
    public static final String CANCEL_SUBSCRIPTION_API = "/api/cancelSubscription.json";

    // Latest APK
    public static final String LATEST_APK_API = "/api/latestAPK.json";

    public static class key {
        // Header
        public static final String XR_MAIL = "X-R-MAIL";
        public static final String XR_AUTH = "X-R-AUTH";
        public static final String XR_DID = "X-R-DID";

        // Social
        public static final String PID = "pid";
        public static final String ACCESS_TOKEN = "at";
        public static final String PID_FACEBOOK = "FACEBOOK";
        public static final String PID_GOOGLE = "GOOGLE";

        // Server client id
//        public static final String SERVER_CLIENT_ID_LOCAL = "917069462497-aa1hduf9dh69niv19aq57hp422vqc5p1.apps.googleusercontent.com";
//        public static final String SERVER_CLIENT_ID_TEST = "917069462497-8adldf383r8d5g5cdj867dpuus444i49.apps.googleusercontent.com";
//        public static final String SERVER_CLIENT_ID_LIVE = "917069462497-62ejpasmc8ptjpujgi207fqsldh2lma3.apps.googleusercontent.com";
//        public static final String SERVER_CLIENT_ID = SERVER_CLIENT_ID_LOCAL;

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

        // Unprocessed
        public static final String UNPROCESSEDCOUNT = "unprocessedCount";

    }
}
