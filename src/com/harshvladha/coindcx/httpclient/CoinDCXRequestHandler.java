package com.harshvladha.coindcx.httpclient;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoinDCXRequestHandler {
    private static final String USER_AGENT = "harshvladha/coindcx-api/1.0.0";

    private OkHttpClient client;


    /** Makes a GET request.
     * @return JSONObject which is received by CoinDCX Trade.
     * @param url is the endpoint to which request has to be sent.
     * @param params is the map of data that has to be sent in query params.
     * @throws IOException is thrown when there is a connection related error.
     * @throws JSONException is thrown for parsing errors.
     * */
    public JSONObject getRequest(String url, Map<String, Object> params) throws IOException, JSONException {
        Request request = createGetRequest(url, params);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new CoinDCXResponseHandler().handle(response, body);
    }

    /** Creates a GET request.
     * @param url is the endpoint to which request has to be done.
     * @param params is the map of data that has to be sent in query params.
     * */
    public Request createGetRequest(String url, Map<String, Object> params) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry: params.entrySet()){
            httpBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
        }

        return new Request.Builder()
                    .url(httpBuilder.build())
                    .header("User-Agent", USER_AGENT)
                    .build();
    }
}
