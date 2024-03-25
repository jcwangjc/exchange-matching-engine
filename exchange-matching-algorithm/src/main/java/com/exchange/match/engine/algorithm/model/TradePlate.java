package com.exchange.match.engine.algorithm.model;

import com.exchange.match.engine.algorithm.enums.OrderDirection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.TreeMap;

/**
 *  @author : laoA
 *  @email : laoa@markcoin.net
 *  @describe : 撮合模型——盘口数据
 */
@Data
@Slf4j
public class TradePlate implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 买方深度, TreeMap<price, num>
     */
    private TreeMap<BigDecimal, BigDecimal> bids;

    /**
     * 卖方深度，TreeMap<price, num>
     */
    private TreeMap<BigDecimal, BigDecimal> asks;

    public TradePlate(){
        //买入从大到小
        this.bids =new TreeMap<>(Comparator.reverseOrder());
        //卖出从小到大
        this.asks =new TreeMap<>(Comparator.naturalOrder());
    }

    /**
     * 获取当前的交易队列
     */
    public TreeMap<BigDecimal, BigDecimal> getOurDepth(OrderDirection orderDirection) {
        return orderDirection == OrderDirection.BUY ? bids : asks;
    }

    /**
     * 获取对手的交易队列
     */
    public TreeMap<BigDecimal, BigDecimal> getOpponentDepth(OrderDirection orderDirection) {
        return orderDirection == OrderDirection.BUY ? asks : bids;
    }

    /**
     * 添加深度数据
     * 新增委托单的时候，没有绝对的成交，这个时候要把剩余数据加入盘口
     * @param order
     */
    public void add(OrderLimit order, int baseCoinScale){
        log.info("trade plate add {} , price {} ,amount {},tradeAmount {},baseCoinScale {}",order.getOrderId(),order.getPrice(),order.getAmount(),order.getTradeAmount(),baseCoinScale);
        TreeMap<BigDecimal, BigDecimal> tradePlate = this.getOurDepth(order.getOrderDirection());
        BigDecimal size = tradePlate.get(order.getPrice());
        //总数量-已完成的数量=盘口需添加的数量
        if(size==null){
            size=BigDecimal.ZERO;
        }
        BigDecimal subtract=order.getAmount().subtract(order.getTradeAmount());
        tradePlate.put(order.getPrice(),size.add(subtract));
    }

    /**
     * 从盘口里移除数据
     * @param order
     * @param reduceAmount
     */
    public void remove(OrderLimit order, BigDecimal reduceAmount, int baseCoinScale){
        log.info("trade plate remove {} ,reduceAmount {} ,baseCoinScale {}",order.getOrderId(),reduceAmount,baseCoinScale);
        TreeMap<BigDecimal, BigDecimal> tradePlate = this.getOurDepth(order.getOrderDirection());
        BigDecimal amount = tradePlate.get(order.getPrice());
        if(amount==null){
            log.error(".......find trade plate is null and price is {} orderid {}", order.getPrice(),order.getOrderId());
            return;
        }
        //盘口数量-成交的数量=盘口剩余数量
        BigDecimal subtract = amount.subtract(reduceAmount);
        //数量为0就移除
        if(subtract.compareTo(BigDecimal.ZERO)<=0){
            tradePlate.remove(order.getPrice());
        }else{
            tradePlate.put(order.getPrice(),subtract);
        }
    }

}

