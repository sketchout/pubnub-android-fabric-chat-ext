package com.pubnub.example.android.fabric.pnfabricchat.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Utility class for converting JSON objects/strings/etc.
 */
public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJSONObject(JSONObject value, Class<T> clazz) throws Exception {
        return mapper.readValue(value.toString(), clazz);
    }

    public static String asJson(Object value) throws Exception {
        if (value instanceof JSONArray || value instanceof JSONObject) {
            return value.toString();
        }

        return mapper.writeValueAsString(value);
    }

    public static JSONObject asJSONObject(Object value) throws Exception {
        return new JSONObject(mapper.writeValueAsString(value));
    }
}
