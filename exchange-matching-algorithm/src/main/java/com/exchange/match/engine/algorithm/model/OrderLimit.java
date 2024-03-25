package com.exchange.match.engine.algorithm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLimit extends Order {
    /**
     * 挂单的价格
     */
    public BigDecimal price = BigDecimal.ZERO;
    /**
     * 买入或者卖出量
     */
    public BigDecimal amount = BigDecimal.ZERO;
    /**
     * 成交量
     */
    protected BigDecimal tradeAmount = BigDecimal.ZERO;

    public Boolean isCompleted(int baseCoinScale){
        BigDecimal min = BigDecimal.ONE.movePointLeft(baseCoinScale);
        BigDecimal check =  this.getAmount().subtract(this.getTradeAmount());
        return check.compareTo(min)<0?true:false;
    }
}
