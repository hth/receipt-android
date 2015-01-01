package com.receiptofi.checkout.http;

/**
 * Created by PT on 12/31/14.
 */
public class HTTPEndpoints {

    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_GET = "GET";

    public static final String CONNECTION_URL_LOCAL = "http://192.168.1.12:9090/receipt-mobile";
    public static final String CONNECTION_URL_STAGING = "https://test.receiptofi.com/receipt-mobile";
    //public static final String CONNECTION_URL_STAGING = CONNECTION_URL_LOCAL;
    public static final String CONNECTION_URL_PRODUCTION = "https://live.receiptofi.com/receipt-mobile";

    public static final String RECEIPTOFI_MOBILE_URL = CONNECTION_URL_STAGING;
}
