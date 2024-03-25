package com.exchange.match.engine.execute.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : laoA
 * @describe : 数据发送，每次发送失败，都把失败日志入库，下游根据日志来进行相关处理
 * @email : laoa@markcoin.net
 */
@Component
@Slf4j
public class RocketMqProducerTemplate {
    @Autowired
    private DefaultMQProducer producer;
    /**
     * 分区投递（同步）
     * @param message 消息体
     * @param id
     * @param topic   主体
     * @throws MQClientException
     * @throws RemotingException
     * @throws InterruptedException
     */
    public SendResult sendMessage(Object message, Long id, String topic, String symbol) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        String body = JSONObject.toJSON(message).toString();
        Message msg = new Message(topic, symbol,"KEY_" + id, body.getBytes());
        SendResult send = producer.send(msg, (mqs, msg1, arg) -> {
            int value = arg.hashCode() % mqs.size();
            if (value < 0) {
                value = Math.abs(value);
            }
            return mqs.get(value);
        }, id);
        return send;
    }

    /**
     * 分区投递(批量)
     * @param topic   主体
     * @throws MQClientException
     * @throws RemotingException
     * @throws InterruptedException
     */
    public void sendMessageBatch(List<Object> messages, Long takerUserId, String topic, String symbol) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        List<Message> batch = new ArrayList<>();
        for (Object message:messages) {
            String body = JSONObject.toJSON(message).toString();
            Message msg = new Message(topic, symbol,"KEY_" + takerUserId, body.getBytes());
            batch.add(msg);
        }
        SendResult send = producer.send(batch);
    }

    /**
     * 异步推送
     * @param message
     * @param id
     * @param topic
     * @param symbol
     * @throws MQClientException
     * @throws RemotingException
     * @throws InterruptedException
     * @throws MQBrokerException
     */
    public void sendAsyncMessage(Object message, Long id, String topic,String symbol) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        String body = JSONObject.toJSON(message).toString();
        Message msg = new Message(topic, symbol,"KEY_" + id, body.getBytes());
        producer.send(msg, (mqs, msg1, arg) -> {
            int value = arg.hashCode() % mqs.size();
            if (value < 0) {
                value = Math.abs(value);
            }
            return mqs.get(value);
        }, id, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {

            }
            @SneakyThrows
            @Override
            public void onException(Throwable throwable) {
                log.error("have error to sync send message is {}, error:{}", JSON.toJSONString(message),throwable.getMessage());
                sendMessage(message, id, topic, symbol);
            }
        });
    }
}
