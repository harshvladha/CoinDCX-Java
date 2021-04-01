# CoinDCX-Java

This repository is made for using two important CoinDCX API
- Historical Candle Data
- Real time Ticks

CoinDCX gives these two as public API but over HTTP or WebSockets, read their [documentation](https://coindcx-official.github.io/rest-api/)

## Usage

### Maven

For latest version, checkout [Packages](https://github.com/harshvladha/CoinDCX-Java/packages)

```xml
<dependency>
    <groupId>com.harshvladha</groupId>
    <artifactId>coindcx</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Historical API

```java
CandleAPI candleAPI = new CandleAPI(); // create new CandleAPI instance 

CoinCandle[] coinCandles = candleAPI.getHistoricalData(Date.from(Instant.ofEpochMilli(1616284800000L)),
        Date.from(Instant.ofEpochMilli(1616371200000L)),
        "B-BTC_USDT",
        "1d"); // fetches historical data for the given time range and candle period (i.e., candle interval)
for (CoinCandle coinCandle : coinCandles) {
    System.out.println(coinCandle);
}
```

### Ticks API

```java
CoinTicker coinTicker = new CoinTicker(); // create new CoinTicker instance (can be used across multi-threads)
coinTicker.connect(); // starts WebSocket

coinTicker.subscribe(new HashSet<>() {{add("B-BTC_USDT");}}); // subscribes for BTC-USDT pair via Binance exchange

coinTicker.setTickListener(System.out::println); // pass a listener so that you can add your business logic
```


## Issue?

If you find any issue or need some help or want to add more features/API, please create Github Issue (no issue format supported yet, just go ahead and create)
