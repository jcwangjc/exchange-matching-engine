package com.exchange.match.engine.execute.test;

import com.alibaba.fastjson.JSONObject;
import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import com.exchange.match.engine.algorithm.model.Order;
import com.exchange.match.engine.algorithm.model.OrderLimit;

import java.math.BigDecimal;

public class MainTest {
    public static void main(String[] args) {
        OrderLimit orderLimit=new OrderLimit();
        orderLimit.setSymbol("BTCUSDT");
        orderLimit.setPrice(new BigDecimal("1000"));
        orderLimit.setAmount(new BigDecimal("10"));
        orderLimit.setOrderDirection(OrderDirection.BUY);
        orderLimit.setOrderType(OrderType.LIMIT);
        orderLimit.setUserId(1l);
        orderLimit.setOrderId(1001l);

        String str = JSONObject.toJSONString(orderLimit);

        Order order = JSONObject.parseObject(str, OrderLimit.class);

        System.out.println(order);
    }
}
