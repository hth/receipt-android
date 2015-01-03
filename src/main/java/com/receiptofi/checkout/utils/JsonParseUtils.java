package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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

    public static Map<String, Map<String, String>> parseReceipt(String jsonResponse) {
        Map<String, Map<String, String>> allReceipts = new HashMap<>();
        try {
            JSONArray receipts = new JSONArray(jsonResponse);
            for (int i = 0; i < receipts.length(); ++i) {
                JSONObject receipt = receipts.getJSONObject(i);
                allReceipts.put(receipt.getString("id"), parseReceiptToMap(receipt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allReceipts;
    }

    private static Map<String, String> parseReceiptToMap(JSONObject receipt) throws JSONException {
        Map<String, String> receiptMap = new HashMap<>();
        receiptMap.put("bizName", receipt.getJSONObject("bizName").getString("name"));
        receiptMap.put("address", receipt.getJSONObject("bizStore").getString("address"));
        receiptMap.put("phone", receipt.getJSONObject("bizStore").getString("phone"));
        receiptMap.put("date", receipt.getString("date"));
        receiptMap.put("expenseReport", receipt.getString("expenseReport"));
        JSONArray files = receipt.getJSONArray("files");
        StringBuilder blobIds = new StringBuilder();
        for (int i = 0; i < files.length(); ++i) {
            if (0 == blobIds.length() ) {
                blobIds.append(files.getJSONObject(i).getString("blobId"));
            } else {
                blobIds.append(",").append(files.getJSONObject(i).getString("blobId"));
            }
        }
        receiptMap.put("blobIds", blobIds.toString());
        receiptMap.put("id", receipt.getString("id"));
        receiptMap.put("notes", receipt.getJSONObject("notes").getString("text"));
        receiptMap.put("ptax", receipt.getString("ptax"));
        receiptMap.put("rid", receipt.getString("rid"));
        receiptMap.put("total", receipt.getString("total"));
        return receiptMap;
    }
}
