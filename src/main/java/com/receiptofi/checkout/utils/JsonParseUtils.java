package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.DataWrapper;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.model.ProfileModel;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.UnprocessedDocumentModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: hitender
 * Date: 1/1/15 11:11 PM
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
            Log.d(TAG, profileModel.toString());
        } catch (JSONException e) {
            Log.d(TAG, "Fail parsing profile response=" + profile, e);
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
        receiptModel.setReceiptDate(receipt.getString("receiptDate"));
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
        receiptModel.setExpenseTagId(receipt.getString("expenseTagId"));
        receiptModel.setBillStatus(receipt.getString("bs"));
        receiptModel.setActive(receipt.getBoolean("a"));
        receiptModel.setDeleted(receipt.getBoolean("d"));

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
                    item.getString("expenseTagId")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing receiptItem response=" + item, e);
            return null;
        }
    }

    public static List<ExpenseTagModel> parseExpenses(JSONArray jsonArray) {
        List<ExpenseTagModel> expenseTagModels = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                expenseTagModels.add(parseExpense(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing expenses reason=" + e.getLocalizedMessage(), e);
        }
        return expenseTagModels;
    }

    public static ExpenseTagModel parseExpense(JSONObject jsonObject) {
        try {
            return new ExpenseTagModel(
                    jsonObject.getString("id"),
                    jsonObject.getString("tag"),
                    jsonObject.getString("color")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing expense response=" + jsonObject, e);
            return null;
        }
    }

    public static List<NotificationModel> parseNotifications(JSONArray jsonArray) {
        List<NotificationModel> notificationModels = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                notificationModels.add(parseNotification(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing notification reason=" + e.getLocalizedMessage(), e);
        }
        return notificationModels;
    }

    public static NotificationModel parseNotification(JSONObject jsonObject) {
        try {
            return new NotificationModel(
                    jsonObject.getString("id"),
                    jsonObject.getString("m"),
                    jsonObject.getBoolean("n"),
                    jsonObject.getString("nt"),
                    jsonObject.getString("ri"),
                    jsonObject.getString("c"),
                    jsonObject.getString("u"),
                    jsonObject.getBoolean("a")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing notification response=" + jsonObject, e);
            return null;
        }
    }

    public static DataWrapper parseData(String jsonResponse) {
        DataWrapper dataWrapper = new DataWrapper();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (!jsonObject.isNull("profile")) {
                ProfileModel profileModel = parseProfile(jsonObject.getJSONObject("profile"));
                if (profileModel != null) {
                    dataWrapper.setProfileModel(profileModel);
                }
            } else {
                Log.d(TAG, "No profile updates");
            }

            List<ReceiptItemModel> receiptItemModels = parseItems(jsonObject.getJSONArray("items"));
            if (!receiptItemModels.isEmpty()) {
                dataWrapper.setReceiptItemModels(receiptItemModels);
            }

            List<ReceiptModel> receiptModels = parseReceipts(jsonObject.getJSONArray("receipts"));
            if (!receiptModels.isEmpty()) {
                dataWrapper.setReceiptModels(receiptModels);
            }

            List<ExpenseTagModel> expenseTagModels = parseExpenses(jsonObject.getJSONArray("expenseTags"));
            if (!expenseTagModels.isEmpty()) {
                dataWrapper.setExpenseTagModels(expenseTagModels);
            }

            UnprocessedDocumentModel unprocessedDocumentModel = parseUnprocessedDocument(jsonObject.getJSONObject("unprocessedDocuments"));
            dataWrapper.setUnprocessedDocumentModel(unprocessedDocumentModel);

            List<NotificationModel> notificationModels = parseNotifications(jsonObject.getJSONArray("notifications"));
            if (!notificationModels.isEmpty()) {
                dataWrapper.setNotificationModels(notificationModels);
            }

            Log.d(TAG, "parsed all data");
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse, e);
            e.printStackTrace();
        }

        return dataWrapper;
    }
}
