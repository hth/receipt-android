package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;

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
                allReceipts.add(parseReceipt(receipt));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing receipt response=" + jsonResponse, e);
        }
        return allReceipts;
    }

    private static ReceiptModel parseReceipt(JSONObject receipt) throws JSONException {
        ReceiptModel receiptModel = new ReceiptModel();

        receiptModel.setBizName(receipt.getJSONObject("bizName").getString("name"));
        receiptModel.setAddress(receipt.getJSONObject("bizStore").getString("address"));
        receiptModel.setPhone(receipt.getJSONObject("bizStore").getString("phone"));
        receiptModel.setDate(receipt.getString("date"));
        receiptModel.setExpenseReport(receipt.getString("expenseReport"));
        JSONArray files = receipt.getJSONArray("files");
        StringBuilder blobIds = new StringBuilder();
        for (int i = 0; i < files.length(); ++i) {
            if (0 == blobIds.length()) {
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

    public static ReceiptItemModel parseReceiptItem(String jsonResponse) {
        try {
            JSONObject item = new JSONObject(jsonResponse);
            return new ReceiptItemModel(
                    item.getString("id"),
                    item.getString("name"),
                    item.getString("price"),
                    item.getString("quantity"),
                    item.getString("receiptId"),
                    item.getString("sequence"),
                    item.getString("tax")
            );
        } catch(JSONException e) {
            Log.e(TAG, "Fail parsing receiptItem response=" + jsonResponse, e);
            return null;
        }
    }

    public static boolean parseDeviceRegistration(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            return json.getBoolean("registered");
        } catch(JSONException e) {
            Log.e(TAG, "Fail parsing deviceRegistration response=" + jsonResponse, e);
            return false;
        }
    }
}
