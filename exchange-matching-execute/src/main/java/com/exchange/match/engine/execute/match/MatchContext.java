package com.exchange.match.engine.execute.match;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.MatchResult;

/**
 * @author : laoA
 * @describe : Match容器，orderBook的出口
 * @email : laoa@markcoin.net
 */
@Slf4j
public class MatchContext{

    /**
     * mq客户端缓存
     */
    public static final Map<String, List<DefaultMQPushConsumer>> consumers=new ConcurrentHashMap();

    public static Long ringBufferDownTime=10000L;

    /**
     * 发送订单交易记录明细——吃单在左，挂单在右
     * 相同的order推送到同一个message queue
     * @param matchResults
     */
    public void sendResult(List<MatchResult> matchResults) {
        System.out.println("====发送消息到下游");
    }
}
