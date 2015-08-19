package com.receiptofi.receiptapp.model;

/**
 * User: hitender
 * Date: 7/9/15 1:58 AM
 */
public interface TransactionDetail {
    enum TYPE {PAY, SUB}

    boolean isSuccess();

    String getMessage();
}
