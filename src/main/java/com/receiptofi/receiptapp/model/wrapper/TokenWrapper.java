package com.receiptofi.receiptapp.model.wrapper;

import com.receiptofi.receiptapp.model.TokenModel;

/**
 * User: hitender
 * Date: 7/6/15 2:20 PM
 */
public class TokenWrapper {
    private static TokenModel tokenModel;

    public static TokenModel getTokenModel() {
        return tokenModel;
    }

    public static void setTokenModel(TokenModel tokenModel) {
        TokenWrapper.tokenModel = tokenModel;
    }
}
