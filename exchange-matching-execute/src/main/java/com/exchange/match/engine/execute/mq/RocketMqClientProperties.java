package com.exchange.match.engine.execute.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *  @describe : mq配置
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@ConfigurationProperties(prefix = "rocket-mq")
@Data
public class RocketMqClientProperties {
    private Producer producer;
    private Consumer consumer;

    @Data
    public static class Producer{
        @Value("name_servers")
        private String nameServers;
        @Value("consumer_group_prefix")
        private String consumerGroupPrefix;
        @Value("subscribe_prefix")
        private String subscribePrefix;
        @Value("default_topic_queue_num")
        private int defaultTopicQueueNum;
        @Value("send_msg_timeout")
        private int sendMsgTimeout;
        @Value("retry_times_when_send_async_failed")
        private int retryTimesWhenSendAsyncFailed;
        @Value("retry_times_when_send_failed")
        private int retryTimesWhenSendFailed;
        @Value("subscribe_prefix_complete_cancel")
        private String subscribePrefixCompleteCancel;
    }

    @Data
    public static class Consumer{
        @Value("name_servers")
        private String nameServers;
        @Value("consumer_group_prefix")
        private String consumerGroupPrefix;
        @Value("subscribe_prefix")
        private String subscribePrefix;
        @Value("timeout")
        private int timeout;
        @Value("pull_interval")
        private int pullInterval;
        @Value("sub_expression")
        private String subExpression;
    }
}
