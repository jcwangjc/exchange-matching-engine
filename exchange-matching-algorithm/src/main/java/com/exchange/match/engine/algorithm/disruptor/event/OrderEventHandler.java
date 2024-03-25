package com.exchange.match.engine.algorithm.disruptor.event;

import com.exchange.match.engine.algorithm.enums.OrderType;
import com.exchange.match.engine.algorithm.factory.MatchService;
import com.exchange.match.engine.algorithm.factory.MatchServiceFactory;
import com.exchange.match.engine.algorithm.model.Order;
import com.exchange.match.engine.algorithm.model.OrderBook;
import com.exchange.match.engine.algorithm.model.TradePlate;
import com.lmax.disruptor.EventHandler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *  describe : 事件处理
 *  * 该对象有多个，和symbol（交易对）的数据对应
 *  * 针对某一个OrderEventHandler，只会同一时间有一个线程来执行它
 *  author : laoA
 *  email : laoa@markcoin.net
 */
@Slf4j
@Data
public class OrderEventHandler implements EventHandler<OrderEvent> {

    private OrderBook orderBooks;

    private String symbol;

    private TradePlate tradePlate;


    public OrderEventHandler(OrderBook orderBooks){
        this.orderBooks=orderBooks;
        this.symbol=orderBooks.getSymbol();
        this.tradePlate=new TradePlate();
    }

    /**
     * 接收到了某个消息
     * @param event
     * @param sequence
     * @param endOfBatch
     * @throws Exception
     */
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        Order order=(Order)event.getSource();
        OrderType orderType = order.getOrderType();
        MatchService matchService = MatchServiceFactory.getByOrderType(orderType);
        matchService.execute(orderBooks,order,tradePlate);
    }
}
