package com.receiptofi.checkout.utils;

import android.util.Log;

import com.receiptofi.checkout.http.API;

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
}
