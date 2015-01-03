package com.receiptofi.checkout.http;

import android.os.Bundle;

import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseParser {

    public static void getLoginDetails(String response) {
        try {
            JSONObject loginResponseJson = new JSONObject(response);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getSocialAuthError(String response) {
        try {
            JSONObject loginResponseJson = new JSONObject(response);

            JSONObject error = loginResponseJson.getJSONObject("error");

            String errorMsg = error.getString("reason");

            return errorMsg;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public synchronized static Bundle getImageUploadResponse(String response) {

        Bundle bundle = new Bundle();
        try {
            JSONObject imageResponse = new JSONObject(response);
            bundle.putString(DatabaseTable.ImageIndex.BLOB_ID, imageResponse.getString("blobId"));

            JSONObject unprocessedDocuments = imageResponse.getJSONObject("unprocessedDocuments");
            bundle.putInt("unprocessedCount", unprocessedDocuments.getInt("unprocessedCount"));

        } catch (JSONException e) {

        }
        return bundle;
    }

    public static ArrayList<ReceiptModel> getReceipts(String response) {

        ArrayList<ReceiptModel> models = new ArrayList<>();
        // ArrayList<Rec>
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                ReceiptModel model = new ReceiptModel();
                // ReceiptModel.bizName.class;

                JSONObject bizNameJson = json.getJSONObject("bizName");
                model.setBizName(bizNameJson.getString("name"));

                JSONObject bizStoreJson = json.getJSONObject("bizStore");
                model.setAddress(bizStoreJson.getString("address"));
                model.setPhone(bizStoreJson.getString("phone"));

                model.setDate(json.getString("date"));
                model.setExpenseReport(json.getString("expenseReport"));

                JSONArray jsonArray = json.getJSONArray("files");

                JSONObject filesJson = (JSONObject) jsonArray.get(0);
                model.setBlobIds(filesJson.getString("blobId")); //ERROR

                model.setId(json.getString("id"));

                JSONObject notesJson = json.getJSONObject("notes");
                model.setNotes(notesJson.getString("text"));

                String ptaxStr = json.getString("ptax");
                if (ptaxStr != null) {
                    model.setPtax(Double.valueOf(ptaxStr));
                }
                model.setRid(json.getString("rid"));
                Double totalStr = json.getDouble("total");
                if (totalStr != null) {
                    model.setTotal(totalStr.doubleValue());
                }
                models.add(model);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return models;
    }


    public static ArrayList<ReceiptElement> getReceiptDetails(String response) {

        ArrayList<ReceiptElement> elements = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                ReceiptElement receiptElement = new ReceiptElement();
                JSONObject receiptElementJson = array.getJSONObject(i);
                receiptElement.quantity = receiptElementJson.getString("quant");
                receiptElement.id = receiptElementJson.getString("id");
                receiptElement.name = receiptElementJson.getString("name");
                receiptElement.price = receiptElementJson.getString("price");
                receiptElement.receipt_id = receiptElementJson.getString("receiptId");
                receiptElement.sequence = receiptElementJson.getString("seq");
                receiptElement.tax = receiptElementJson.getString("tax");

                elements.add(receiptElement);
            }
        } catch (JSONException e) {
            return null;
        }

        return elements;
    }

}
