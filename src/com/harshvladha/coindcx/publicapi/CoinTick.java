package com.harshvladha.coindcx.publicapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public class CoinTick {
    @JsonProperty("T")
    private Long epochTime;
    @JsonProperty("m")
    private Boolean marketMaker;
    @JsonProperty("p")
    private BigDecimal tradePrice;
    @JsonProperty("q")
    private BigDecimal quantity;
    @JsonProperty("channel")
    private String channel;

}
