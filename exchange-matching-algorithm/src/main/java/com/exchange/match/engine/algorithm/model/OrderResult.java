package com.exchange.match.engine.algorithm.model;

import com.exchange.match.engine.algorithm.enums.OrderDirection;
import lombok.Builder;
import lombok.Data;

/**
 * @author : laoA
 * @describe : 撮合模型——订单撮合结果
 * @email : laoa@markcoin.net
 */
@Data
@Builder
public class OrderResult {
    /**
     * take订单id
     */
    private Long orderId;
    /**
     * taker用户id
     */
    private Long userId;
    /**
     * 订单是否完成
     */
    private Boolean finished;
    /**
     * 方向
     */
    private OrderDirection orderDirection;
}
