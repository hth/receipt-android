package com.receiptofi.checkout.model;

import com.receiptofi.checkout.model.types.MobileSystemErrorCodeEnum;

/**
 * User: hitender
 * Date: 7/21/15 10:19 PM
 */
public class ErrorModel {
    private String reason;
    private MobileSystemErrorCodeEnum systemErrorCode;
    private String systemError;

    public ErrorModel(String reason, String systemErrorCode, String systemError) {
        this.reason = reason;
        this.systemErrorCode = MobileSystemErrorCodeEnum.valueOf(systemErrorCode);
        this.systemError = systemError;
    }

    public String getReason() {
        return reason;
    }

    public MobileSystemErrorCodeEnum getSystemErrorCode() {
        return systemErrorCode;
    }

    public String getSystemError() {
        return systemError;
    }

    @Override
    public String toString() {
        return "ErrorModel{" +
                "reason='" + reason + '\'' +
                ", systemErrorCode=" + systemErrorCode +
                ", systemError='" + systemError + '\'' +
                '}';
    }
}
