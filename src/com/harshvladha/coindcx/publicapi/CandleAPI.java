package com.harshvladha.coindcx.publicapi;

import com.harshvladha.coindcx.httpclient.CoinDCXRequestHandler;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;

public class CandleAPI {
    private static final String HOST = "https://public.coindcx.com";
    private static final String ENDPOINT = "/market_data/candles";

    /** Retrieves historical data for an instrument.
     * @param from "yyyy-mm-dd" for fetching candles between days and "yyyy-mm-dd hh:mm:ss" for fetching candles between timestamps.
     * @param to "yyyy-mm-dd" for fetching candles between days and "yyyy-mm-dd hh:mm:ss" for fetching candles between timestamps.
     * @param token is instruments token.
     * @param interval can be minute, day, 3minute, 5minute, 10minute, 15minute, 30minute, 60minute.
     * @return HistoricalData object which contains list of historical data termed as dataArrayList.
     * @throws IOException is thrown when there is connection related error.
     * */
    public CoinCandle[] getHistoricalData(Date from, Date to, String token, String interval) throws IOException, JSONException {
        
        Map<String, Object> params = new HashMap<>();
        params.put("startTime", from.toInstant().toEpochMilli());
        params.put("endTime", to.toInstant().toEpochMilli());
        params.put("pair", token);
        params.put("interval", interval);

        CoinDCXRequestHandler requestHandler = new CoinDCXRequestHandler();
        return requestHandler.getRequest(HOST+ENDPOINT, params, CoinCandle[].class);
    }
}
