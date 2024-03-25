package com.exchange.match.engine.execute.boot;

import com.exchange.match.engine.algorithm.disruptor.manager.DisruptorManager;
import com.exchange.match.engine.algorithm.disruptor.template.DisruptorTemplate;
import com.exchange.match.engine.execute.match.MatchContext;
import com.exchange.match.engine.execute.match.MatchEngineProperties;
import com.exchange.match.engine.execute.mq.MatchListener;
import com.exchange.match.engine.execute.mq.RocketMqClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.Set;

/**
 *  @describe : 启动项
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
@Configuration
@EnableConfigurationProperties(value = {MatchEngineProperties.class, RocketMqClientProperties.class})
@Slf4j
public class SymbolsCmdRunner extends MatchContext implements CommandLineRunner, DisposableBean {

    @Autowired
    private DisruptorManager disruptorManager;

    private MatchEngineProperties matchEngineProperties;

    private RocketMqClientProperties rocketMqClientProperties;

    public SymbolsCmdRunner(MatchEngineProperties matchEngineProperties,RocketMqClientProperties rocketMqClientProperties) {
        this.matchEngineProperties = matchEngineProperties;
        this.rocketMqClientProperties=rocketMqClientProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, MatchEngineProperties.CoinScale> symbols = matchEngineProperties.getSymbols();
        initSymbols(symbols);
    }

    @Async
    public void initSymbols(Map<String, MatchEngineProperties.CoinScale> symbols){
        Set<Map.Entry<String, MatchEngineProperties.CoinScale>> entries = symbols.entrySet();
        if (entries == null || entries.size() == 0) {
            return;
        }
        entries.forEach(kv -> {
            try{
                initSymbol(kv);
            }catch (Exception e){
                e.printStackTrace();
                log.error("initSymbol error {}",kv);
            }
        });
    }

    public void initSymbol(Map.Entry<String, MatchEngineProperties.CoinScale> kv) throws Exception{
        DisruptorTemplate disruptorTemplate = initBook(kv);
        //3.处理mq中的数据
        initMQData(kv.getKey(),rocketMqClientProperties.getConsumer(),disruptorTemplate);
    }

    private DisruptorTemplate initBook(Map.Entry<String, MatchEngineProperties.CoinScale> kv){
        String symbol = kv.getKey();
        DisruptorTemplate disruptorTemplate = disruptorManager.build(symbol,kv.getValue().getCoinScale(),kv.getValue().getCoinScale());
        log.info(".......init disruptor --> order_book and local_TradePlate with symbol as {}",symbol);
        return disruptorTemplate;
    }

    private void initMQData(String symbol, RocketMqClientProperties.Consumer _config,DisruptorTemplate disruptorTemplate) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(_config.getConsumerGroupPrefix()+symbol.toUpperCase());
        consumer.setNamesrvAddr(_config.getNameServers());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);//如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setMessageModel(MessageModel.CLUSTERING);
        consumer.setConsumeTimeout(_config.getTimeout());//超时时间(分钟)
        consumer.setPullInterval(_config.getPullInterval());//拉取的时间间隔
        String topic=_config.getSubscribePrefix()+symbol.toUpperCase();//主题
        consumer.subscribe(topic, _config.getSubExpression());
        consumer.setMaxReconsumeTimes(_config.getReconsume()); //消费失败的重试次数
        consumer.setPullBatchSize(100);
        consumer.setConsumeMessageBatchMaxSize(1000);

        //顺序消费
        MatchListener matchListener=new MatchListener();
        matchListener.setDisruptorTemplate(disruptorTemplate);
        consumer.registerMessageListener(matchListener);
        consumer.start();
    }

    @Override
    public void destroy() throws Exception {

    }

}
