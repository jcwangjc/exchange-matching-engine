package com.exchange.match.engine.execute.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.invoke.MethodHandles;

/**
 *  @describe : mq producer初始化
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@Configuration
@EnableConfigurationProperties(value = {RocketMqClientProperties.class})
public class RocketMqProducerConfig implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RocketMqClientProperties rocketMQClientProperties;

    public RocketMqProducerConfig(RocketMqClientProperties rocketMQClientProperties){
        this.rocketMQClientProperties=rocketMQClientProperties;
    }

    private DefaultMQProducer producer;

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        try{
            logger.info("..........load rocket mq producer rocketMQClientProperties--->{}",rocketMQClientProperties);
            RocketMqClientProperties.Producer _producer_config = rocketMQClientProperties.getProducer();
            DefaultMQProducer producer = new DefaultMQProducer(_producer_config.getConsumerGroupPrefix());
            producer.setNamesrvAddr(_producer_config.getNameServers());
            producer.setDefaultTopicQueueNums(_producer_config.getDefaultTopicQueueNums());
            producer.setSendMsgTimeout(_producer_config.getSendMsgTimeout());
            producer.setRetryTimesWhenSendAsyncFailed(_producer_config.getRetryTimesWhenSendAsyncFailed());
            producer.setRetryTimesWhenSendFailed(_producer_config.getRetryTimesWhenSendFailed());
            producer.setRetryAnotherBrokerWhenNotStoreOK(true);
            producer.start();
            this.producer=producer;
            return producer;
        }catch (MQClientException e){
            logger.info("..........load rocket mq producer info");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destroy() throws Exception {
        this.producer.shutdown();
        logger.info(".......close rocket mq producer.....");
    }
}
