package com.harshvladha.coindcx.httpclient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Response handler for handling all the responses.
 */
public class CoinDCXResponseHandler {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String JSON = "json";

    public JSONObject handle(Response response, String body) throws JSONException {
        if (null != response.header(CONTENT_TYPE) && response.header(CONTENT_TYPE).contains(JSON)) {
            return new JSONObject(body);
        }

        throw new JSONException("Unknown Response Format");
    } 
}
