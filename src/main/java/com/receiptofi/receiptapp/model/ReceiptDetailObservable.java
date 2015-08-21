package com.receiptofi.receiptapp.model;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/20/15 1:38 PM
 */
public class ReceiptDetailObservable extends DataSetObservable {
    private static final String TAG = ReceiptDetailObservable.class.getSimpleName();

    private static List<DataSetObserver> observerList = new ArrayList<>();

    private ReceiptDetailObservable() {
    }

    public static ReceiptDetailObservable getInstance() {
        return new ReceiptDetailObservable();
    }

    public static synchronized void refreshReceiptModel() {
        Log.d(TAG, "ReceiptDetail changed, notify, number of observers=" + observerList.size());
        for (DataSetObserver observer : observerList) {
            // TODO :fixme by fixing the trigger for update
            observer.onChanged();
        }
    }

    @Override
    public void registerObserver(DataSetObserver observer) {
        Log.d(TAG, "observer registration");
        observerList.add(observer);
        Log.d(TAG, "observer registered. Total number of observers: " + mObservers.size());
    }

    @Override
    public void unregisterObserver(DataSetObserver observer) {
        Log.d(TAG, "unregister observer");
        observerList.remove(observer);
        Log.d(TAG, "unregister observer DONE. Total number of observers: " + mObservers.size());
    }
}
