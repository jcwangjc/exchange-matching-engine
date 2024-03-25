package com.exchange.match.engine.algorithm.factory;


import com.exchange.match.engine.algorithm.enums.OrderType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  @describe : 撮合工厂
 *  @author : laoA
 *  @email : laoa@markcoin.net
 */
public class MatchServiceFactory {
    private static Map<OrderType, MatchService> services = new ConcurrentHashMap<OrderType, MatchService>();

    public  static MatchService getByOrderType(OrderType orderType){
        return services.get(orderType);
    }

    public static void register(OrderType orderType, MatchService matchService){
        services.put(orderType, matchService);
    }
}
