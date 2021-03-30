package com.harshvladha.coindcx.httpclient;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Response handler for handling all the responses.
 */
public class CoinDCXResponseHandler {
    
    public JSONObject handle(Response response, String body) throws IOException, JSONException {
        if (response.header("Content-Type").contains("json")) {
            return new JSONObject(body);
        }

        throw new JSONException("Unknown Response Format")
    } 
}
