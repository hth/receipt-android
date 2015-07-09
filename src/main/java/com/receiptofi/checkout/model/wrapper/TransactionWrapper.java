package com.receiptofi.checkout.model.wrapper;

import com.receiptofi.checkout.model.TransactionDetail;

/**
 * User: hitender
 * Date: 7/9/15 1:50 AM
 */
public class TransactionWrapper {
    private TransactionDetail transactionDetail;

    public TransactionWrapper(TransactionDetail transactionDetail) {
        this.transactionDetail = transactionDetail;
    }

    public TransactionDetail getTransactionDetail() {
        return transactionDetail;
    }
}
