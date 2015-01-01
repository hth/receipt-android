package com.receiptofi.checkout.http;

import android.os.Bundle;

import com.receiptofi.checkout.db.ReceiptDB;
import com.receiptofi.checkout.models.ReceiptElement;
import com.receiptofi.checkout.models.ReceiptModel;

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
            bundle.putString(ReceiptDB.ImageIndex.BLOB_ID, imageResponse.getString("blobId"));

            JSONObject unprocessedDocuments = imageResponse.getJSONObject("unprocessedDocuments");
            bundle.putInt("unprocessedCount", unprocessedDocuments.getInt("unprocessedCount"));

        } catch (JSONException e) {

        }
        return bundle;
    }

    public static ArrayList<ReceiptModel> getReceipts(String response) {

        ArrayList<ReceiptModel> models = new ArrayList<ReceiptModel>();
        // ArrayList<Rec>
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                ReceiptModel model = new ReceiptModel();
                // ReceiptModel.bizName.class;

                JSONObject bizNameJson = json.getJSONObject("bizName");
                model.bizName = bizNameJson.getString("name");

                JSONObject bizStoreJson = json.getJSONObject("bizStore");
                model.bizStoreAddress = bizStoreJson.getString("address");
                model.bizStorePhone = bizStoreJson.getString("phone");

                model.date = json.getString("date");
                model.expenseReport = json.getString("expenseReport");

                JSONArray jsonArray = json.getJSONArray("files");

                JSONObject filesJson = (JSONObject) jsonArray.get(0);
                model.filesBlobId = filesJson.getString("blobId");
                model.filesOrientation = filesJson.getString("orientation");
                model.filesSequence = filesJson.getString("sequence");

                model.id = json.getString("id");

                JSONObject notesJson = json.getJSONObject("notes");
                model.notesText = notesJson.getString("text");

                String ptaxStr = json.getString("ptax");
                if (ptaxStr != null) {
                    model.ptax = Double.valueOf(ptaxStr);
                }
                String ridStr = json.getString("rid");
                if (ridStr != null) {
                    model.rid = Long.valueOf(ridStr);
                }
                Double totalStr = json.getDouble("total");
                if (totalStr != null) {
                    model.total = totalStr.doubleValue();
                }
                models.add(model);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return models;
    }


    public static ArrayList<ReceiptElement> getReceiptDetails(String response) {

        ArrayList<ReceiptElement> elemets = new ArrayList<ReceiptElement>();
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

                elemets.add(receiptElement);
            }
        } catch (JSONException e) {
            return null;
        }

        return elemets;
    }

}
