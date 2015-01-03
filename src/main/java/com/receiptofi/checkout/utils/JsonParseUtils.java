package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.models.ReceiptModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hitender on 1/1/15.
 */
public class JsonParseUtils {

    private static final String TAG = JsonParseUtils.class.getSimpleName();

    public static Map<String, String> parseUnprocessedCount(String jsonResponse) {
        Map<String, String> map = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            map.put(API.key.UNPROCESSEDCOUNT, jsonObject.getString(API.key.UNPROCESSEDCOUNT));
        } catch (JSONException e) {
            Log.d(TAG, "Fail parsing " + API.key.UNPROCESSEDCOUNT + " response=" + jsonResponse, e);
            map.put("unprocessedCount", "0");
        }
        return map;
    }

    public static List<ReceiptModel> parseReceipt(String jsonResponse) {
        List<ReceiptModel> allReceipts = new LinkedList<>();
        try {
            JSONArray receipts = new JSONArray(jsonResponse);
            for (int i = 0; i < receipts.length(); ++i) {
                JSONObject receipt = receipts.getJSONObject(i);
                allReceipts.add(parseReceiptToMap(receipt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allReceipts;
    }

    private static ReceiptModel parseReceiptToMap(JSONObject receipt) throws JSONException {
        ReceiptModel receiptModel = new ReceiptModel();

        receiptModel.setBizName(receipt.getJSONObject("bizName").getString("name"));
        receiptModel.setAddress(receipt.getJSONObject("bizStore").getString("address"));
        receiptModel.setPhone(receipt.getJSONObject("bizStore").getString("phone"));
        receiptModel.setDate(receipt.getString("date"));
        receiptModel.setExpenseReport(receipt.getString("expenseReport"));
        JSONArray files = receipt.getJSONArray("files");
        StringBuilder blobIds = new StringBuilder();
        for (int i = 0; i < files.length(); ++i) {
            if (0 == blobIds.length() ) {
                blobIds.append(files.getJSONObject(i).getString("blobId"));
            } else {
                blobIds.append(",").append(files.getJSONObject(i).getString("blobId"));
            }
        }
        receiptModel.setBlobIds(blobIds.toString());
        receiptModel.setId(receipt.getString("id"));
        receiptModel.setNotes(receipt.getJSONObject("notes").getString("text"));
        receiptModel.setPtax(receipt.getDouble("ptax"));
        receiptModel.setRid(receipt.getString("rid"));
        receiptModel.setTotal(receipt.getDouble("total"));
        return receiptModel;
    }
}
