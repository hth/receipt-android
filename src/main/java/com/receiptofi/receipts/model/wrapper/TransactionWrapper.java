package com.receiptofi.receipts.model.wrapper;

import com.receiptofi.receipts.model.TransactionDetail;

/**
 * User: hitender
 * Date: 7/9/15 1:50 AM
 */
public class TransactionWrapper {
    private static TransactionDetail transactionDetail;

    public static void setTransactionDetail(TransactionDetail transactionDetail) {
        TransactionWrapper.transactionDetail = transactionDetail;
    }

    public static TransactionDetail getTransactionDetail() {
        return transactionDetail;
    }
}