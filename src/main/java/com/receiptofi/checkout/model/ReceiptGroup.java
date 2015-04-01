package com.receiptofi.checkout.model;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/2/15 6:44 AM
 */
public class ReceiptGroup extends DataSetObservable{

    private static final String TAG = ReceiptGroup.class.getSimpleName();

    private List<List<ReceiptModel>> receiptModels = new LinkedList<>();
    private List<ReceiptGroupHeader> receiptGroupHeaders = new LinkedList<>();

    private static List<DataSetObserver> observerList = new ArrayList<>();
    private static ReceiptGroup instance;

    private ReceiptGroup(){
        super();
    }

    public static synchronized ReceiptGroup getInstance(){
        if (instance == null){
            return new ReceiptGroup();
        }
        return instance;
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

    public void addReceiptGroup(List<ReceiptModel> receiptModel) {
        this.receiptModels.add(receiptModel);
        Log.d(TAG, " ReceiptGroup has changed");
        Log.d(TAG, "Time to notify. Total number of observers: " + observerList.size());
        for (DataSetObserver observer : observerList){
            // TODO :fixme by fixing the trigger for update
            observer.onChanged();
        }
    }

    public List<List<ReceiptModel>> getReceiptModels() {
        return receiptModels;
    }

    public List<ReceiptGroupHeader> getReceiptGroupHeaders() {
        return receiptGroupHeaders;
    }

    public void addReceiptGroupHeader(ReceiptGroupHeader receiptGroupHeader) {
        this.receiptGroupHeaders.add(receiptGroupHeader);
    }
}
