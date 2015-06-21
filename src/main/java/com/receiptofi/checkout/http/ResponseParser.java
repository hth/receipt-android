package com.receiptofi.checkout.http;

import android.os.Bundle;

import com.receiptofi.checkout.db.DatabaseTable;
import com.receiptofi.checkout.model.ReceiptModel;
import com.receiptofi.checkout.utils.ConstantsJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

            JSONObject unprocessedDocuments = imageResponse.getJSONObject(ConstantsJson.UNPROCESSED_DOCUMENTS);
            bundle.putInt(ConstantsJson.UNPROCESSED_COUNT, unprocessedDocuments.getInt(ConstantsJson.UNPROCESSED_COUNT));
            bundle.putString(ConstantsJson.UPLOADED_DOCUMENT_NAME, imageResponse.getString(ConstantsJson.UPLOADED_DOCUMENT_NAME));

        } catch (JSONException e) {
            //TODO added log message
        }
        return bundle;
    }

    public static List<ReceiptModel> getReceipts(String response) {
        List<ReceiptModel> models = new ArrayList<>();
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

                model.setReceiptDate(json.getString("receiptDate"));
                model.setExpenseReport(json.getString("expenseReport"));

                JSONArray jsonArray = json.getJSONArray("files");

                JSONObject filesJson = (JSONObject) jsonArray.get(0);
                model.setBlobIds(filesJson.getString("blobId")); //ERROR

                model.setId(json.getString("id"));

                JSONObject notesJson = json.getJSONObject("notes");
                model.setNotes(notesJson.getString("text"));

                String ptax = json.getString("ptax");
                if (null != ptax) {
                    model.setPtax(Double.valueOf(ptax));
                }
                model.setRid(json.getString("rid"));
                Double total = json.getDouble("total");
                model.setTotal(total);
                models.add(model);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return models;
    }
}
