package com.exchange.match.engine.algorithm.disruptor.template;

import com.exchange.match.engine.algorithm.disruptor.event.OrderEvent;
import com.exchange.match.engine.algorithm.model.Order;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 *  describe : 发送模版
 *  author : laoA
 *  email : laoa@markcoin.net
 */
public class DisruptorTemplate {

    private static final EventTranslatorOneArg<OrderEvent, Order> TRANSLATOR = new EventTranslatorOneArg<OrderEvent, Order>() {
        @Override
        public void translateTo(OrderEvent event, long sequence, Order input) {
            event.setSource(input);
        }
    };

    private final RingBuffer<OrderEvent> ringBuffer;

    public DisruptorTemplate(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 我们使用DisruptorTemplate 时,就使用它的onData方法
     * @param input
     */
    public void onData(Order input) {
        ringBuffer.publishEvent(TRANSLATOR, input);
    }

    public RingBuffer<OrderEvent> getRingBuffer(){
        return ringBuffer;
    }
}

