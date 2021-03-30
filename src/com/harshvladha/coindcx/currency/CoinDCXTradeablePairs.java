package com.harshvladha.coindcx.currency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoinDCXTradeablePairs {
    private final List<String> tradeablePairs;
    private final Map<String, CoinDCXCurrencyPair> pairEnumMap;
    
    public CoinDCXTradeablePairs() throws IOException {
        this.tradeablePairs = Files.readAllLines(Paths.get("res/tradeable_pairs"));
        this.pairEnumMap = new HashMap<>();

        for (String tradeablePair: tradeablePairs) {
			    String cleanPair = tradeablePair.split(" ")[0];
                CoinDCXCurrencyPair enumPair = CoinDCXCurrencyPair.valueOf(cleanPair.replace("/", "_"));
			    pairEnumMap.put(cleanPair.replace("/", ""), enumPair);
        }
    }


    public CoinDCXCurrencyPair getCoinDCXCurrencyPair(String pair) {
        return pairEnumMap.get(pair);
	  }
}
