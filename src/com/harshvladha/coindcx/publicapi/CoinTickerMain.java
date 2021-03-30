package com.harshvladha.coindcx.publicapi;

import java.util.ArrayList;

public class CoinTickerMain {
    public static void main(String[] args) throws InterruptedException {
        CoinTicker coinTicker = new CoinTicker();
        coinTicker.connect();
        coinTicker.subscribe(new ArrayList<>() {{add("B-BTC_USDT");}});
        Thread.sleep(100000000);
    }
}
