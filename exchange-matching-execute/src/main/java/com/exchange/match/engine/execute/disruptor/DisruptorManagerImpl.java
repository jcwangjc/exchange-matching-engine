package com.exchange.match.engine.execute.disruptor;

import com.exchange.match.engine.algorithm.disruptor.manager.DisruptorManager;
import com.exchange.match.engine.algorithm.model.OrderBook;
import org.springframework.stereotype.Service;

/**
 *  @describe : 驱动
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@Service
public class DisruptorManagerImpl extends DisruptorManager {
    @Override
    protected OrderBook buildOrderBook(String symbol, int priceScala, int coinScale) {
        return new OrderBook(symbol,priceScala,coinScale);
    }
}
