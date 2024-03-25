package com.exchange.match.engine.algorithm.utils;

import java.math.BigDecimal;

/**
 *  @author : laoA
 *  @email : laoa@markcoin.net
 *  @describe : 精度检查
 */
public class ScalaCheckUtil {
    /**
     * 判断剩余数量，是否比最小精度还要小
     * @param baseCoinScale
     * @param size
     * @return
     */
    public static Boolean isCompleted(int baseCoinScale,BigDecimal size){
        //最小精度
        BigDecimal min = BigDecimal.ONE.movePointLeft(baseCoinScale);
        return size.compareTo(min)<0?true:false;
    }
}
