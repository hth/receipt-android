package com.receiptofi.receiptapp.http;

import android.os.Bundle;
import android.util.Log;

import com.receiptofi.receiptapp.db.DatabaseTable;
import com.receiptofi.receiptapp.utils.ConstantsJson;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseParser {
    private static final String TAG = ResponseParser.class.getSimpleName();

    private ResponseParser() {
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
            Log.d(TAG, "reason=" + e.getMessage(), e);
        }
        return bundle;
    }
}
