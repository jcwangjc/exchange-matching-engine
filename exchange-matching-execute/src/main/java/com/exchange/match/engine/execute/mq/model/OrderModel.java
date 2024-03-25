package com.exchange.match.engine.execute.mq.model;

import com.exchange.match.engine.algorithm.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 *  @describe : 订单模型
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@Data
@Builder
@AllArgsConstructor
public class OrderModel {
    /**
     * 1限价 2市价 3撤单
     */
    private OrderType orderType;

    /**
     * 订单json字符串
     */
    private String content;
}
