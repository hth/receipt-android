package com.receiptofi.checkout.utils;

import android.text.TextUtils;
import android.util.Log;

import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.model.BillingAccountModel;
import com.receiptofi.checkout.model.BillingHistoryModel;
import com.receiptofi.checkout.model.ErrorModel;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.NotificationModel;
import com.receiptofi.checkout.model.PlanModel;
import com.receiptofi.checkout.model.ProfileModel;
import com.receiptofi.checkout.model.ReceiptItemModel;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.model.TokenModel;
import com.receiptofi.checkout.model.TransactionDetail;
import com.receiptofi.checkout.model.TransactionDetailPaymentModel;
import com.receiptofi.checkout.model.TransactionDetailSubscriptionModel;
import com.receiptofi.checkout.model.UnprocessedDocumentModel;
import com.receiptofi.checkout.model.wrapper.DataWrapper;
import com.receiptofi.checkout.model.wrapper.PlanWrapper;
import com.receiptofi.checkout.model.wrapper.TokenWrapper;
import com.receiptofi.checkout.model.wrapper.TransactionWrapper;

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
        if (receipt.getJSONObject("bizStore").has("lat")) {
            receiptModel.setLat(receipt.getJSONObject("bizStore").getString("lat"));
        }
        if (receipt.getJSONObject("bizStore").has("lng")) {
            receiptModel.setLng(receipt.getJSONObject("bizStore").getString("lng"));
        }
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
                    item.getDouble("price"),
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
                    jsonObject.getString("color"),
                    jsonObject.getBoolean("d")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing expense response=" + jsonObject + " reason=" + e.getLocalizedMessage(), e);
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

    public static BillingAccountModel parseBilling(JSONObject jsonObject) {
        try {
            BillingAccountModel billingAccountModel = new BillingAccountModel(jsonObject.getString("bt"));
            if (jsonObject.has("billingHistories")) {
                billingAccountModel.setBillingHistories(parseBillingHistories(jsonObject.getJSONArray("billingHistories")));
            }
            return billingAccountModel;
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing billing account response=" + jsonObject + " reason=" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static List<BillingHistoryModel> parseBillingHistories(JSONArray jsonArray) {
        List<BillingHistoryModel> billingHistoryModels = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                BillingHistoryModel billingHistoryModel = parseBillingHistory(jsonArray.getJSONObject(i));
                if (null != billingHistoryModel) {
                    billingHistoryModels.add(billingHistoryModel);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing notification reason=" + e.getLocalizedMessage(), e);
        }
        return billingHistoryModels;
    }

    public static BillingHistoryModel parseBillingHistory(JSONObject jsonObject) {
        try {
            return new BillingHistoryModel(
                    jsonObject.getString("id"),
                    jsonObject.getString("bm"),
                    jsonObject.getString("bs"),
                    jsonObject.getString("bt"),
                    jsonObject.getString("bd")
            );
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing billing account response=" + jsonObject + "reason=" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static DataWrapper parseData(String jsonResponse) {
        DataWrapper dataWrapper = new DataWrapper();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (!jsonObject.isNull(ConstantsJson.PROFILE)) {
                ProfileModel profileModel = parseProfile(jsonObject.getJSONObject(ConstantsJson.PROFILE));
                if (null != profileModel) {
                    dataWrapper.setProfileModel(profileModel);
                }
            } else {
                Log.d(TAG, "No " + ConstantsJson.PROFILE + " updates");
            }

            List<ReceiptItemModel> receiptItemModels = parseItems(jsonObject.getJSONArray(ConstantsJson.ITEMS));
            if (!receiptItemModels.isEmpty()) {
                dataWrapper.setReceiptItemModels(receiptItemModels);
            }

            List<ReceiptModel> receiptModels = parseReceipts(jsonObject.getJSONArray(ConstantsJson.RECEIPTS));
            if (!receiptModels.isEmpty()) {
                dataWrapper.setReceiptModels(receiptModels);
            }

            List<ExpenseTagModel> expenseTagModels = parseExpenses(jsonObject.getJSONArray(ConstantsJson.EXPENSE_TAGS));
            if (!expenseTagModels.isEmpty()) {
                dataWrapper.setExpenseTagModels(expenseTagModels);
            }

            UnprocessedDocumentModel unprocessedDocumentModel = parseUnprocessedDocument(jsonObject.getJSONObject(ConstantsJson.UNPROCESSED_DOCUMENTS));
            dataWrapper.setUnprocessedDocumentModel(unprocessedDocumentModel);

            List<NotificationModel> notificationModels = parseNotifications(jsonObject.getJSONArray(ConstantsJson.NOTIFICATIONS));
            if (!notificationModels.isEmpty()) {
                dataWrapper.setNotificationModels(notificationModels);
            }

            if (!jsonObject.isNull(ConstantsJson.BILLING)) {
                BillingAccountModel billingAccountModel = parseBilling(jsonObject.getJSONObject(ConstantsJson.BILLING));
                dataWrapper.setBillingAccountModel(billingAccountModel);
            }

            Log.d(TAG, "parsed all data");
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
        return dataWrapper;
    }

    public static String parseForErrorReason(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject errorJson = jsonObject.getJSONObject("error");
            String errorReason = errorJson.getString("reason");
            Log.d(TAG, "errorReason: " + errorReason);
            return errorReason;
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static ErrorModel parseError(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject errorJson = jsonObject.getJSONObject("error");
            String errorReason = errorJson.has("reason") ? errorJson.getString("reason") : "";
            String errorCode = errorJson.has("systemErrorCode") ? errorJson.getString("systemErrorCode") : "";
            String error = errorJson.has("systemError") ? errorJson.getString("systemError") : "";

            Log.d(TAG, "errorReason: " + errorReason);
            return new ErrorModel(errorReason, errorCode, error);
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static void parsePlan(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            List<PlanModel> planModels = new LinkedList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                planModels.add(parsePlanModel(jsonArray.getJSONObject(i)));
            }
            PlanWrapper.setPlanModels(planModels);
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
    }

    private static PlanModel parsePlanModel(JSONObject jsonObject) {
        try {
            return new PlanModel(
                    jsonObject.getString("id"),
                    jsonObject.getDouble("price"),
                    jsonObject.getString("billingFrequency"),
                    jsonObject.getString("description"),
                    jsonObject.getString("billingDayOfMonth"),
                    jsonObject.getString("name"),
                    jsonObject.getString("paymentGateway"),
                    jsonObject.getString("billingPlan"));
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing billing account response=" + jsonObject + "reason=" + e.getLocalizedMessage(), e);
            return null;
        }
    }

    public static void parseToken(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.getBoolean("hasCustomerInfo")) {
                TokenWrapper.setTokenModel(
                        new TokenModel(
                                jsonObject.getString("token"),
                                jsonObject.getBoolean("hasCustomerInfo"),
                                jsonObject.getString("firstName"),
                                jsonObject.getString("lastName"),
                                jsonObject.getString("postalCode"),
                                jsonObject.getString("planId")));
            } else {
                TokenWrapper.setTokenModel(
                        new TokenModel(
                                jsonObject.getString("token"),
                                jsonObject.getBoolean("hasCustomerInfo"),
                                null,
                                null,
                                null,
                                null));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static void parseTransaction(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            switch (TransactionDetail.TYPE.valueOf(jsonObject.getString("type"))) {
                case PAY:
                    TransactionWrapper.setTransactionDetail(
                            new TransactionDetailPaymentModel(
                                    TransactionDetail.TYPE.PAY,
                                    jsonObject.getBoolean("success"),
                                    jsonObject.getString("status"),
                                    jsonObject.getString("firstName"),
                                    jsonObject.getString("lastName"),
                                    jsonObject.getString("postalCode"),
                                    jsonObject.getString("accountPlanId"),
                                    jsonObject.getString("transactionId"),
                                    jsonObject.getString("message")));
                    break;
                case SUB:
                    TransactionWrapper.setTransactionDetail(
                            new TransactionDetailSubscriptionModel(
                                    TransactionDetail.TYPE.SUB,
                                    jsonObject.getBoolean("success"),
                                    jsonObject.getString("status"),
                                    jsonObject.getString("planId"),
                                    jsonObject.getString("firstName"),
                                    jsonObject.getString("lastName"),
                                    jsonObject.getString("postalCode"),
                                    jsonObject.getString("accountPlanId"),
                                    jsonObject.getString("subscriptionId"),
                                    jsonObject.getString("message")));
                    break;
                default:
                    Log.e(TAG, "Undefined Transaction Type=" + jsonObject.getString("type"));
                    throw new RuntimeException("Undefined Transaction Type");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
    }

    public static String parseLatestAPK(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            if (jsonObject.has("apk")) {
                return jsonObject.getString("apk");
            }
        } catch (JSONException e) {
            Log.e(TAG, "Fail parsing jsonResponse=" + jsonResponse + " reason=" + e.getLocalizedMessage(), e);
        }
        return "";
    }
}
