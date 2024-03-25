package com.exchange.match.engine.execute.match.impl;

import com.exchange.match.engine.algorithm.factory.MatchService;
import com.exchange.match.engine.algorithm.model.OrderBook;
import com.exchange.match.engine.algorithm.model.OrderMarket;
import com.exchange.match.engine.algorithm.model.TradePlate;
import com.exchange.match.engine.execute.match.MatchContext;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author : laoA
 * @describe : 市价交易撮合逻辑
 * @email : laoa@markcoin.net
 */
public class MatchMarketServiceImpl extends MatchContext implements MatchService<OrderMarket>, InitializingBean {
    @Override
    public void execute(OrderBook orderBook, OrderMarket order, TradePlate tradePlate) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
