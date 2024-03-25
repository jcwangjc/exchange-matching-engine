package com.exchange.match.engine.algorithm.disruptor.exception;


import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.LifecycleAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 *  describe : disruptor异常处理器
 *  author : laoA
 *  email : laoa@markcoin.net
 */
public class DisruptorHandlerException implements ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * <p>Strategy for handling uncaught exceptions when processing an event.</p>
     *
     * <p>If the strategy wishes to terminate further processing by the {@link BatchEventProcessor}
     * then it should throw a {@link RuntimeException}.</p>
     *
     * @param ex       the exception that propagated from the {@link EventHandler}.
     * @param sequence of the event which cause the exception.
     * @param event    being processed when the exception occurred.  This can be null.
     */
    @Override
    public void handleEventException(Throwable ex, long sequence, Object event) {
        logger.info("handleEventException Exception===>{} , sequence==> {} ,event===>{}",ex.getMessage(),sequence,event);
    }

    /**
     * Callback to notify of an exception during {@link LifecycleAware#onStart()}
     *
     * @param ex throw during the starting process.
     */
    @Override
    public void handleOnStartException(Throwable ex) {
        logger.info("OnStartHandler Exception===>{} ",ex.getMessage());
    }

    /**
     * Callback to notify of an exception during {@link LifecycleAware#onShutdown()}
     *
     * @param ex throw during the shutdown process.
     */
    @Override
    public void handleOnShutdownException(Throwable ex) {
        logger.info("OnShutdownHandler Exception===>{} ",ex.getMessage());
    }
}
