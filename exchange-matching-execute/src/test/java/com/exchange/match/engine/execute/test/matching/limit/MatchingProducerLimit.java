package com.exchange.match.engine.execute.test.matching.limit;

import com.alibaba.fastjson.JSONObject;
import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import com.exchange.match.engine.algorithm.model.OrderLimit;
import com.exchange.match.engine.execute.mq.model.OrderModel;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : laoA
 * @describe : 限价交易
 * @email : laoa@markcoin.net
 */
public class MatchingProducerLimit {
    public static void main(String[] args) throws Exception {
        // 实例化消息生产者Producer
        DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
        // 设置NameServer的地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        // 设置消息同步发送失败时的重试次数，默认为 2
        producer.setRetryTimesWhenSendFailed(2);
        // 设置消息发送超时时间，默认3000ms
        producer.setSendMsgTimeout(3000);
        // 启动Producer实例
        producer.start();

        List<OrderModel> takers = getTakers();
        for(OrderModel order:takers){
            try{
                send(order,producer);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        // 如果不再发送消息，关闭Producer实例。
        producer.shutdown();
    }

    /**
     * 限价交易taker
     * @return
     */
    private static List<OrderModel> getTakers(){
        List<OrderModel> result=new ArrayList<>();
        Integer price=1000;
        for (int i=0;i<10;i++){
            OrderLimit orderLimit=new OrderLimit();
            orderLimit.setPrice(new BigDecimal(price.toString()));
            orderLimit.setAmount(new BigDecimal("10"));
            orderLimit.setSymbol("BTCUSDT");
            orderLimit.setOrderDirection(OrderDirection.BUY);
            orderLimit.setUserId(1l+i);
            orderLimit.setOrderId(1001l+i);

            String jsonString = JSONObject.toJSONString(orderLimit);

            OrderModel orderModel = OrderModel.builder().content(jsonString).orderType(OrderType.LIMIT).build();
            orderModel.setOrderType(OrderType.LIMIT);
            result.add(orderModel);
            price+=1;
        }
        return result;
    }


    private static void send(OrderModel order,DefaultMQProducer producer) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        // 创建消息，并指定Topic，Tag和消息体
        Object o = JSONObject.toJSON(order);
        Message msg = new Message("matching_BTCUSDT",
                "TagA" ,
                o.toString().getBytes(RemotingHelper.DEFAULT_CHARSET)
        );
        // 发送消息到一个Broker
        SendResult sendResult = producer.send(msg);
        // 通过sendResult返回消息是否成功送达
        System.out.printf("%s%n", sendResult);
    }
}
