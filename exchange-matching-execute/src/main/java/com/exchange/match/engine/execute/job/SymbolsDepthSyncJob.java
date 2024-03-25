package com.exchange.match.engine.execute.job;

import com.exchange.match.engine.algorithm.disruptor.manager.DisruptorManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author : laoA
 * @describe : 同步盘口数据
 * @email : laoa@markcoin.net
 */
@Slf4j
@Component
public class SymbolsDepthSyncJob {
    @Autowired
    private DisruptorManager disruptorManager;

    @Scheduled(fixedDelay = 500)
    public void syncData() {

    }
}
