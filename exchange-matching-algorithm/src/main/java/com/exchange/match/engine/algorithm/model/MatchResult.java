package com.exchange.match.engine.algorithm.model;


import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : laoA
 * @describe : 撮合模型——成交记录
 * @email : laoa@markcoin.net
 */
@Data
public class MatchResult {
    //交易对
    private String symbol;
    //交易对-商户隔离
    private String symbolWassName;
    //交易方向taker为准
    private OrderDirection orderDirection;
    //交易价格-成交价
    private BigDecimal price;
    //交易的数量
    private BigDecimal tradeCount;
    //交易吃单的订单id
    private Long takerOrderId;
    //交易挂单的订单id
    private Long makerOrderId;
    //交易吃单的用户id
    private Long takerUserId;
    //交易挂单的用户id
    private Long makerUserId;
    //挂单的成交额
    private BigDecimal buyTurnover;
    //出售方的成交额
    private BigDecimal sellTurnover;
    //成交时间
    private Long time;
    //maker订单结束标识 true订单结束 false正常
    private Boolean makerIsFinish =false;
    //taker订单结束标识 true订单结束 false正常
    private Boolean takerIsFinish =false;
    //剩余数量
    private BigDecimal cancelledCount=BigDecimal.ZERO;
    //剩余金额
    private BigDecimal cancelledAmount=BigDecimal.ZERO;

    private OrderType orderType;

    private Long takerBusinessId;

    private Long makerBusinessId;

    /**
     * 是否完全撤销，没有一点成交
     */
    private Boolean  completeCancel=false;

    /**
     * 消息时间戳 毫秒
     */
    private Long timeStamp = null;


    private String takerOrderSourceType;


    private String makerOrderSourceType;


    private Integer takerOrderSourceTypeMark;

    private Integer makerOrderSourceTypeMark;

    /**
     * 订单方向 1 BUY 2 SELL
     */
    private Integer takerOrderDirection;

    /**
     * 订单方向 1 BUY 2 SELL
     */
    private Integer makerOrderDirection;

    private Integer makerTradeId;

}
