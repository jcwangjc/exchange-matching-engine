package com.exchange.match.engine.execute.match;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 *  @describe : 撮合信息配置
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@ConfigurationProperties(prefix = "match",ignoreInvalidFields=true)
public class MatchEngineProperties {
    /**
     * 交易对的信息
     */
    private Map<String,CoinScale> symbols=new HashMap<>();

    @Data
    public static class CoinScale{
        //价格精度
        private int priceScale;
        //币种精度
        private int coinScale;
    }

    public Map<String, CoinScale> getSymbols() {
        return symbols;
    }

    public void setSymbols(Map<String, CoinScale> symbols) {
        this.symbols = symbols;
    }
}
