package com.exchange.match.engine.execute.boot;

import com.exchange.match.engine.algorithm.disruptor.manager.DisruptorManager;
import com.exchange.match.engine.algorithm.disruptor.template.DisruptorTemplate;
import com.exchange.match.engine.algorithm.model.TradePlate;
import com.exchange.match.engine.execute.match.MatchContext;
import com.exchange.match.engine.execute.match.MatchEngineProperties;
import lombok.extern.slf4j.Slf4j;
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
@EnableConfigurationProperties(value = {MatchEngineProperties.class})
@Slf4j
public class SymbolsCmdRunner extends MatchContext implements CommandLineRunner, DisposableBean {

    @Autowired
    private DisruptorManager disruptorManager;

    private MatchEngineProperties matchEngineProperties;

    public SymbolsCmdRunner(MatchEngineProperties matchEngineProperties) {
        this.matchEngineProperties = matchEngineProperties;
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
//        initMQData(kv.getKey(),rocketMQClientProperties.getConsumer(),disruptorTemplate);
    }

    private DisruptorTemplate initBook(Map.Entry<String, MatchEngineProperties.CoinScale> kv){
        String symbol = kv.getKey();
        DisruptorTemplate disruptorTemplate = disruptorManager.build(symbol,kv.getValue().getCoinScale(),kv.getValue().getCoinScale());
        log.info(".......init disruptor --> order_book and local_TradePlate with symbol as {}",symbol);
        return disruptorTemplate;
    }

    @Override
    public void destroy() throws Exception {

    }

}
