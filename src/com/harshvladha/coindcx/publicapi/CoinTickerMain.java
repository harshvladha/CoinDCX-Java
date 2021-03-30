package com.harshvladha.coindcx.publicapi;

import java.util.ArrayList;
import java.util.HashSet;

public class CoinTickerMain {
    public static void main(String[] args) throws InterruptedException {
        CoinTicker coinTicker = new CoinTicker();
        coinTicker.connect();
        coinTicker.subscribe(new HashSet<>() {{add("B-BTC_USDT");}});
        coinTicker.setTickListener(System.out::println);
        Thread.sleep(10000);
        coinTicker.unsubscribe(new ArrayList<>() {{add("B-BTC_USDT");}});
    }
}
