package com.exchange.match.engine.execute.mq;

import com.exchange.match.engine.algorithm.disruptor.template.DisruptorTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author harrison
 */
@Slf4j
@Data
public class MatchListener implements MessageListenerOrderly {

    private DisruptorTemplate disruptorTemplate;

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext consumeOrderlyContext) {
        consumeOrderlyContext.setAutoCommit(true);
        long currentTime = System.currentTimeMillis();
        for (MessageExt msg : msgs) {

//            if (msg.getReconsumeTimes()>0){
//                log.error("message reconsumed msgId {},keys {}",msg.getMsgId(),msg.getKeys());
//                continue;
//            }
//            MatchMessage matchMessage =  JSONObject.parseObject(new String(msg.getBody()), MatchMessage.class);
//            if (matchMessage.getTimeStamp()==0){
//                continue;
//            }else{
//                if (Math.subtractExact(currentTime,matchMessage.getTimeStamp())>maxDelay){
//                    log.error("message is overtimes msgId {},keys {}",msg.getMsgId(),msg.getKeys());
//                    continue;
//                }
//            }
//            if (matchMessage!=null) {
//                for (MQEntrust message : matchMessage.getMessages()) {
//                    try {
//                        MatchOrder order = OrderConvertUtil.convert(message);
//                        order.setSource(OrderSource.MQ);
//                        log.info("......deal message order--->{}", order);
//                        //每次都做幂等校验
//                        if (cache != null) {
//                            String key = order.getOrderId() + "_" + order.getMatchType();
//                            Boolean hasDealt = cache.get(key, Boolean.class);
//                            if (hasDealt != null && hasDealt) {
//                                log.error("order {} matchType {} has dealt ,ignore current message", order.getOrderId(), order.getMatchType());
//                            } else {
//                                cache.put(key,Boolean.TRUE);
//                                disruptorTemplate.onData(order);
//                            }
//                        } else {
//                            disruptorTemplate.onData(order);
//                        }
//                    } catch (Exception e) {
//                        log.error(".....deal SptOrder error SptOrder {}", message);
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
