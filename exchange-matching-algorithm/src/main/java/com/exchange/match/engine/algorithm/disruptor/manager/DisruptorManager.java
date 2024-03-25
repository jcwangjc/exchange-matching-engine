package com.exchange.match.engine.algorithm.disruptor.manager;

import com.exchange.match.engine.algorithm.disruptor.event.ClearingEventHandler;
import com.exchange.match.engine.algorithm.disruptor.event.OrderEvent;
import com.exchange.match.engine.algorithm.disruptor.event.OrderEventHandler;
import com.exchange.match.engine.algorithm.disruptor.exception.DisruptorHandlerException;
import com.exchange.match.engine.algorithm.disruptor.template.DisruptorTemplate;
import com.exchange.match.engine.algorithm.model.OrderBook;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 *  describe : 构建disruptor，一个symbol对应一个disruptor，避免disruptor过大,同时提高并发
 *  author : laoA
 *  email : laoa@markcoin.net
 */
@Data
public abstract class DisruptorManager {

    private Map<String,OrderEventHandler> eventHandlerMap=new ConcurrentHashMap<>();

    /**
     * 初始化
     * @param symbol
     * @return
     */
    public RingBuffer<OrderEvent> initRingBuffer(String symbol,int priceScale,int coinScale){
        //1.event事件工厂
        EventFactory<OrderEvent> eventFactory = new EventFactory<OrderEvent>() {
            @Override
            public OrderEvent newInstance() {
                return new OrderEvent();
            }
        };
        //2.等待策略
//        YieldingWaitStrategy waitStrategy = new YieldingWaitStrategy(); //性能高，吃cpu
        BlockingWaitStrategy waitStrategy= new BlockingWaitStrategy(); //利用锁和等待机制的WaitStrategyCPU消耗少但是延迟比较高
        //3.对象
        Disruptor<OrderEvent> disruptor = null;
        //4.因为rocketmq是多线程消费的，所在是多生产者，不能该为单生产者，否则会出现数据丢失的问题
        ProducerType producerType = ProducerType.MULTI;
        //5.构建
        disruptor = new Disruptor<OrderEvent>(eventFactory, 1024*4, Executors.defaultThreadFactory(), producerType, waitStrategy);
        //5-1.设置异常处理对象
        disruptor.setDefaultExceptionHandler(new DisruptorHandlerException());
        //5-2.获取order book
        OrderBook orderBooks=buildOrderBook(symbol,priceScale,coinScale);
        //5-3.撮合事件
        OrderEventHandler orderEventHandler = new OrderEventHandler(orderBooks);
        eventHandlerMap.put(symbol,orderEventHandler);
        //5-4.添加handle
        disruptor.handleEventsWith(orderEventHandler).then(new ClearingEventHandler());
        //5-5.创建环形队列
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        //5-6.开始监听
        disruptor.start();
        //6.对象添加到缓存
        return ringBuffer;
    }

    /**
     * 获取
     * @param symbol
     * @return
     */
    protected abstract OrderBook buildOrderBook(String symbol,int coinScale,int baseCoinScale);

    public DisruptorTemplate build(String symbol, int priceScale, int coinScale){
        RingBuffer<OrderEvent> orderEventRingBuffer = this.initRingBuffer(symbol,priceScale,coinScale);
        DisruptorTemplate disruptorTemplate = new DisruptorTemplate(orderEventRingBuffer);
        return disruptorTemplate;
    }
}
