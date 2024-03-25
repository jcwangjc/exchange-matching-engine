package com.exchange.match.engine.execute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


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
}
