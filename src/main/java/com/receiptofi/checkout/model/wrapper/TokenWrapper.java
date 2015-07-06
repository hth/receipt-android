package com.receiptofi.checkout.model.wrapper;

import com.receiptofi.checkout.model.TokenModel;

import org.joda.time.DateTime;

/**
 * User: hitender
 * Date: 7/6/15 2:20 PM
 */
public class TokenWrapper {

    private static TokenModel tokenModel;
    private static DateTime lastUpdated;

    public static TokenModel getTokenModel() {
        return tokenModel;
    }

    public static void setTokenModel(TokenModel tokenModel) {
        TokenWrapper.tokenModel = tokenModel;
        TokenWrapper.lastUpdated = DateTime.now();
    }

    public static DateTime getLastUpdated() {
        return lastUpdated;
    }
}
