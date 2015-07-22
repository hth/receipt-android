package com.receiptofi.checkout.model;

/**
 * User: hitender
 * Date: 7/21/15 10:19 PM
 */
public class ErrorModel {
    private String reason;
    private int systemErrorCode;
    private String systemError;

    public ErrorModel(String reason, int systemErrorCode, String systemError) {
        this.reason = reason;
        this.systemErrorCode = systemErrorCode;
        this.systemError = systemError;
    }

    public String getReason() {
        return reason;
    }

    public int getSystemErrorCode() {
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
