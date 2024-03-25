package com.exchange.match.engine.algorithm.factory;


import com.exchange.match.engine.algorithm.model.OrderBook;
import com.exchange.match.engine.algorithm.model.TradePlate;

/**
 * @author : laoA
 * @describe : 定义撮合的接口
 * @email : laoa@markcoin.net
 */
public interface MatchService<T> {
    void execute(OrderBook orderBook, T order, TradePlate tradePlate);
}
