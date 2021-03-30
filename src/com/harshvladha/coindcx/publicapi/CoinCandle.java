package com.harshvladha.coindcx.publicapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoinCandle {
    private double open;
    private double close;
    private double low;
    private double high;
    private double volume;
    private double time;
}
