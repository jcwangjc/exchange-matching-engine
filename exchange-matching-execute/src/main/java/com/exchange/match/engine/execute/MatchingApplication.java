package com.exchange.match.engine.execute;

import com.exchange.match.engine.algorithm.disruptor.manager.DisruptorManager;
import com.exchange.match.engine.algorithm.model.OrderBook;
import com.exchange.match.engine.algorithm.model.TradePlate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 *  @author : laoA
 *  @describe : 程序启动以后，通过SymbolsCmdRunner类来加载symbol启动项
 *  @email : laoa@markcoin.net
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class MatchingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MatchingApplication.class, args);
        log.info("........system startup completed");
    }

    @RestController
    public class EchoController {
        @Autowired
        DisruptorManager disruptorManager;

        @GetMapping(value = "/books/{symbol}")
        @ResponseBody
        public OrderBook books(@PathVariable String symbol) {
            OrderBook orderBook = disruptorManager.getEventHandlerMap().get(symbol).getOrderBooks();
            return orderBook;
        }

        @GetMapping(value = "/tradePlate/{symbol}")
        @ResponseBody
        public TradePlate tradePlate(@PathVariable String symbol) {
            TradePlate tradePlate = disruptorManager.getEventHandlerMap().get(symbol).getTradePlate();
            return tradePlate;
        }
    }
}
