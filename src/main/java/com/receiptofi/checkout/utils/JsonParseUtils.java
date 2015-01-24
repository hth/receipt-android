package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.DataWrapper;
import com.receiptofi.checkout.model.ProfileModel;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.UnprocessedDocumentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hitender on 1/1/15.
 */
public class JsonParseUtils {

    private static final String TAG = JsonParseUtils.class.getSimpleName();

    public static UnprocessedDocumentModel parseUnprocessedDocument(String jsonResponse) {
        UnprocessedDocumentModel unprocessedDocumentModel = new UnprocessedDocumentModel(String.valueOf(BigInteger.ZERO));
        try {
            unprocessedDocumentModel = parseUnprocessedDocument(new JSONObject(jsonResponse));
        } catch (JSONException e) {
            Log.d(TAG, "Fail parsing " + API.key.UNPROCESSEDCOUNT + " response=" + jsonResponse, e);
        }
        return unprocessedDocumentModel;
    }

    public static UnprocessedDocumentModel parseUnprocessedDocument(JSONObject unprocessedDocument) {
        UnprocessedDocumentModel unprocessedDocumentModel = new UnprocessedDocumentModel(String.valueOf(BigInteger.ZERO));
        try {
            unprocessedDocumentModel = new UnprocessedDocumentModel(unprocessedDocument.getString(API.key.UNPROCESSEDCOUNT));
        } catch (JSONException e) {
            Log.d(TAG, "Fail parsing " + API.key.UNPROCESSEDCOUNT + " response=" + unprocessedDocument, e);
        }
        return unprocessedDocumentModel;
    }

    public static ProfileModel parseProfile(JSONObject profile) {
        ProfileModel profileModel = null;
        try {
            profileModel = new ProfileModel(
                    profile.getString("firstName"),
                    profile.getString("mail"),
                    profile.getString("rid"));

            profileModel.setLastName(profile.getString("lastName"));
            profileModel.setName(profile.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return profileModel;
    }

    public static boolean parseDeviceRegistration(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            return json.getBoolean("registered");
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing deviceRegistration response=" + jsonResponse, e);
            return false;
        }
    }

    public static List<ReceiptModel> parseReceipts(String jsonResponse) {
        List<ReceiptModel> allReceipts = new LinkedList<>();
        try {
            allReceipts = parseReceipts(new JSONArray(jsonResponse));
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing receipt response=" + jsonResponse, e);
        }
        return allReceipts;
    }

    public static List<ReceiptModel> parseReceipts(JSONArray receipts) {
        List<ReceiptModel> allReceipts = new LinkedList<>();
        try {
            for (int i = 0; i < receipts.length(); ++i) {
                JSONObject receipt = receipts.getJSONObject(i);
                allReceipts.add(parseReceipt(receipt));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing receipt response=" + receipts, e);
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
        receiptModel.setTax(receipt.getDouble("tax"));
        receiptModel.setRid(receipt.getString("rid"));
        receiptModel.setTotal(receipt.getDouble("total"));
        receiptModel.setExpenseTagId(receipt.getString("tagId"));

        return receiptModel;
    }

    public static List<ReceiptItemModel> parseItems(String jsonResponse) {
        List<ReceiptItemModel> receiptItemModels = new ArrayList<>();
        try {
            receiptItemModels = parseItems(new JSONArray(jsonResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return receiptItemModels;
    }

    public static List<ReceiptItemModel> parseItems(JSONArray jsonArray) {
        List<ReceiptItemModel> receiptItemModels = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                receiptItemModels.add(parseItem(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return receiptItemModels;
    }

    public static ReceiptItemModel parseItem(JSONObject item) {
        try {
            return new ReceiptItemModel(
                    item.getString("id"),
                    item.getString("name"),
                    item.getString("price"),
                    item.getString("quant"),
                    item.getString("receiptId"),
                    item.getString("seq"),
                    item.getString("tax"),
                    item.getString("expenseTag")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing receiptItem response=" + item, e);
            return null;
        }
    }

    public static DataWrapper parseData(String jsonResponse) {
        DataWrapper dataWrapper = new DataWrapper();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            ProfileModel profileModel = parseProfile(jsonObject.getJSONObject("profile"));
            if (profileModel != null) {
                dataWrapper.setProfileModel(profileModel);
            }

            List<ReceiptItemModel> receiptItemModels = parseItems(jsonObject.getJSONArray("items"));
            if (!receiptItemModels.isEmpty()) {
                dataWrapper.setReceiptItemModels(receiptItemModels);
            }

            List<ReceiptModel> receiptModels = parseReceipts(jsonObject.getJSONArray("receipts"));
            if (!receiptModels.isEmpty()) {
                dataWrapper.setReceiptModels(receiptModels);
            }

            UnprocessedDocumentModel unprocessedDocumentModel = parseUnprocessedDocument(jsonObject.getJSONObject("unprocessedDocuments"));
            dataWrapper.setUnprocessedDocumentModel(unprocessedDocumentModel);

            Log.d(TAG, "parsed all data");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataWrapper;
    }
}
