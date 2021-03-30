package com.harshvladha.coindcx.httpclient;

import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class CoinDCXRequestHandler {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36";

    private static final OkHttpClient client;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000L, TimeUnit.MILLISECONDS);
        client = builder.build();
    }



    /** Makes a GET request.
     * @return JSONObject which is received by CoinDCX Trade.
     * @param url is the endpoint to which request has to be sent.
     * @param params is the map of data that has to be sent in query params.
     * @param responseType
     * @throws IOException is thrown when there is a connection related error.
     * @throws JSONException is thrown for parsing errors.
     * */
    public <D> D getRequest(String url, Map<String, Object> params, Class<D> responseType) throws IOException, JSONException {
        Request request = createGetRequest(url, params);
        Response response = client.newCall(request).execute();
        String body = response.body().string();
        return new CoinDCXResponseHandler().handle(body, responseType);
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
