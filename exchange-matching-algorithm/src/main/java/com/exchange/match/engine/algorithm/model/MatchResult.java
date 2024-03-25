package com.exchange.match.engine.algorithm.model;


import com.exchange.match.engine.algorithm.enums.CompleteStatus;
import com.exchange.match.engine.algorithm.enums.OrderType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : laoA
 * @describe : 撮合模型——成交记录
 * @email : laoa@markcoin.net
 */
@Data
@Builder
public class MatchResult {
    /**
     * 交易对
     */
    private String symbol;
    /**
     * 交易类型
     */
    private OrderType orderType;
    /**
     * 成交状态
     */
    private CompleteStatus completeStatus;
    /**
     * 生成时间
     */
    private long matchTime;
    /**
     * 撮合成交的价格，取maker价格，保护taker
     */
    private BigDecimal matchPrice;
    /**
     * 撮合成交的数量
     */
    private BigDecimal matchAmount;
    /**
     * taker撮合结果
     */
    private OrderResult takerResult;
    /**
     * maker撮合结果
     */
    private OrderResult makerResult;

}
