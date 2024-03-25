package com.exchange.match.engine.algorithm.model;

import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author : laoA
 * @describe : 撮合模型——委托单
 * @email : laoa@markcoin.net
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class Order implements Serializable {
    /**
     * 本次订单id
     */
    protected Long orderId;
    /**
     * 用户id
     */
    protected Long userId;
    /**
     * symbol交易对
     */
    protected String symbol;
    /**
     * 订单类型，1买单 2卖单
     */
    protected OrderDirection orderDirection;
    /**
     * 1限价 2市价 3撤单
     */
    protected OrderType orderType;

}
