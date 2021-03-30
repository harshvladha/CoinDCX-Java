package com.harshvladha.coindcx.publicapi;

import java.io.IOException;
import java.util.Date;
import java.time.Instant;

public class CandleAPIMain {
    public static void main(String[] args) throws IOException {
        CandleAPI candleAPI = new CandleAPI();

        CoinCandle[] coinCandles = candleAPI.getHistoricalData(Date.from(Instant.ofEpochMilli(1616284800000L)),
                Date.from(Instant.ofEpochMilli(1616371200000L)),
                "B-BTC_USDT",
                "1d");
        for (CoinCandle coinCandle : coinCandles) {
            System.out.println(coinCandle);
        }
    }
}
