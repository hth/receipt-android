package com.receiptofi.checkout.model;

import com.receiptofi.checkout.model.types.MobileSystemErrorCodeEnum;

/**
 * User: hitender
 * Date: 7/21/15 10:19 PM
 */
public class ErrorModel {
    private String reason;
    private int systemErrorCode;
    private MobileSystemErrorCodeEnum errorCode;

    public ErrorModel(String reason, int systemErrorCode, String systemError) {
        this.reason = reason;
        this.systemErrorCode = systemErrorCode;
        this.errorCode = MobileSystemErrorCodeEnum.valueOf(systemError);
    }

    public String getReason() {
        return reason;
    }

    public int getSystemErrorCode() {
        return systemErrorCode;
    }

    public MobileSystemErrorCodeEnum getErrorCode() {
        return errorCode;
    }
}
