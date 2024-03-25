package com.exchange.match.engine.execute.test;

import com.alibaba.fastjson.JSONObject;
import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import com.exchange.match.engine.algorithm.model.Order;
import com.exchange.match.engine.algorithm.model.OrderLimit;
import com.exchange.match.engine.execute.mq.model.OrderModel;

import java.math.BigDecimal;

public class MainTest {
    public static void main(String[] args) {
        OrderLimit orderLimit=new OrderLimit();
        orderLimit.setPrice(new BigDecimal("1000"));
        orderLimit.setAmount(new BigDecimal("10"));
        orderLimit.setSymbol("BTCUSDT");
        orderLimit.setOrderDirection(OrderDirection.BUY);
        orderLimit.setUserId(1l);
        orderLimit.setOrderId(1001l);

        String str = JSONObject.toJSONString(orderLimit);

        OrderModel orderModel = OrderModel.builder().content(str).orderType(OrderType.LIMIT).build();
    }
}
