package com.exchange.match.engine.execute.mq;

import com.alibaba.fastjson.JSONObject;
import com.exchange.match.engine.algorithm.disruptor.template.DisruptorTemplate;
import com.exchange.match.engine.algorithm.model.Order;
import com.exchange.match.engine.algorithm.model.OrderLimit;
import com.exchange.match.engine.execute.mq.model.OrderModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 *  @describe : mq监听
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@Slf4j
@Data
public class RocketMqMatchListener implements MessageListenerOrderly {

    private DisruptorTemplate disruptorTemplate;

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> messages, ConsumeOrderlyContext consumeOrderlyContext) {
        consumeOrderlyContext.setAutoCommit(true);
        for (MessageExt msg : messages) {
            try{
                OrderModel orderModel = JSONObject.parseObject(new String(msg.getBody()), OrderModel.class);
                log.info("...accept orderModel -> {}",JSONObject.toJSONString(orderModel));
                Order order=null;
                switch (orderModel.getOrderType()){
                    case MARKET:
                    case CANCEL:
                        break;
                    case LIMIT:
                        order=JSONObject.parseObject(orderModel.getContent(), OrderLimit.class);
                        break;
                }
                order.setOrderType(orderModel.getOrderType());
                disruptorTemplate.onData(order);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
