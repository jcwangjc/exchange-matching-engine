package com.exchange.match.engine.algorithm.disruptor.event;

import com.lmax.disruptor.EventHandler;

/**
 * @author harrison
 */
public class ClearingEventHandler implements EventHandler<OrderEvent> {
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.clear();
    }
}
