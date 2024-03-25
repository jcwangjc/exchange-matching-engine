package com.exchange.match.engine.algorithm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 *  @author : laoA
 *  @email : laoa@markcoin.net
 *  @describe : 撮合模型——限价单交易模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLimit extends Order {
    /**
     * 挂单的价格
     */
    private BigDecimal price = BigDecimal.ZERO;
    /**
     * 买入或者卖出量
     */
    private BigDecimal amount = BigDecimal.ZERO;
    /**
     * 成交量
     */
    private BigDecimal tradeAmount = BigDecimal.ZERO;
    /**
     * 未成交量
     */
    private BigDecimal UnFinishAmount = BigDecimal.ZERO;
    /**
     * 成交额
     */
    private BigDecimal turnover = BigDecimal.ZERO;
    /**
     * 剩余的金额（限价单按金额购买）
     */
    private BigDecimal surplusFrozen = BigDecimal.ZERO;

}
