package com.receiptofi.receiptapp.model;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 8/23/15 11:19 AM
 */
public class FilterGroupObservable extends DataSetObservable {
    private static final String TAG = ReceiptGroupObservable.class.getSimpleName();

    private static String keyWord = "";
    private static ReceiptGroup monthlyReceiptGroup;
    private static List<DataSetObserver> observerList = new ArrayList<>();

    private FilterGroupObservable() {
    }

    public static FilterGroupObservable getInstance() {
        return new FilterGroupObservable();
    }

    public static ReceiptGroup getMonthlyReceiptGroup() {
        if (monthlyReceiptGroup == null) {
            return ReceiptGroup.getInstance();
        }
        return monthlyReceiptGroup;
    }

    public static String getKeyWord() {
        return keyWord;
    }

    public static void setKeyWord(String keyWord) {
        FilterGroupObservable.keyWord = keyWord;
    }

    public static synchronized void setMonthlyReceiptGroup(ReceiptGroup monthlyReceiptGroup) {
        if (monthlyReceiptGroup != null) {
            FilterGroupObservable.monthlyReceiptGroup = monthlyReceiptGroup;
            Log.d(TAG, "ReceiptGroup changed, notify, number of observers=" + observerList.size());
            for (DataSetObserver observer : observerList) {
                // TODO :fixme by fixing the trigger for update
                observer.onChanged();
            }
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
