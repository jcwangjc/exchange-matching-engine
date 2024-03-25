package com.exchange.match.engine.execute.match.impl;

import com.exchange.match.engine.algorithm.enums.CompleteStatus;
import com.exchange.match.engine.algorithm.enums.OrderDirection;
import com.exchange.match.engine.algorithm.enums.OrderType;
import com.exchange.match.engine.algorithm.factory.MatchService;
import com.exchange.match.engine.algorithm.factory.MatchServiceFactory;
import com.exchange.match.engine.algorithm.model.*;
import com.exchange.match.engine.algorithm.utils.ScalaCheckUtil;
import com.exchange.match.engine.execute.match.MatchContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author : laoA
 * @describe : 限价交易撮合逻辑
 * @email : laoa@markcoin.net
 */
@Slf4j
@Service
public class MatchLimitServiceImpl extends MatchContext implements MatchService<OrderLimit>, InitializingBean {


    @Override
    public void execute(OrderBook orderBook, OrderLimit order, TradePlate tradePlate) {
        List<MatchResult> trades = doMatch(orderBook, order,tradePlate);
    }

    public List<MatchResult> doMatch(OrderBook orderBook, OrderLimit taker,TradePlate tradePlate) {
        List<MatchResult> trades = new ArrayList<>();
        if (orderAmountIllegal(orderBook, taker)) {
            MatchResult trade = generateCancelOrderExchangeTrade(taker);
            trades.add(trade);
            return trades;
        }
        //2、获取对手深度
        OrderDirection orderDirection = taker.getOrderDirection();
        Iterator<Map.Entry<BigDecimal, OrderMerge<OrderLimit>>> iterator = orderBook.getDepthIterator(orderDirection);
        //外循环结束标识
        boolean loop = true;
        //3、循环队列，开始吃单匹配
        while (iterator.hasNext() && loop) {
            Map.Entry<BigDecimal, OrderMerge<OrderLimit>> makerEntity = iterator.next();
            BigDecimal makerPrice = makerEntity.getKey();
            OrderMerge<OrderLimit> makerMergeOrder = makerEntity.getValue();
            if (priceNotMatch(taker, makerPrice)) {
                break;
            }
            //取FIFO订单数据
            Iterator<Long> levelIterator = makerMergeOrder.iterator();
            // 横向吃单
            while (levelIterator.hasNext()) {
                OrderLimit maker = makerMergeOrder.getOrders().get(levelIterator.next());
                //进行吃单操作
                MatchResult matchResult = processMath(taker, maker, orderBook,tradePlate);
                trades.add(matchResult);
                //makerMergeOrder里面的订单被吃掉
                if(ScalaCheckUtil.isCompleted(orderBook.getBaseCoinScale(),maker.getAmount().subtract(maker.getTradeAmount()))){
                    levelIterator.remove();
                }
                //经过一圈的吃单，订单完成退出循环
                if (ScalaCheckUtil.isCompleted(orderBook.getBaseCoinScale(),taker.getAmount().subtract(taker.getTradeAmount()))) {
                    loop = false;
                    break;
                }
            }
            //如果makerMergeOrder被吃空了，就从账本中删除
            if (makerMergeOrder.size() == 0) {
                iterator.remove();
            }
        }
        //4、如果订单没有完成,则加入到账本里面
        if(!ScalaCheckUtil.isCompleted(orderBook.getBaseCoinScale(),taker.getAmount().subtract(taker.getTradeAmount()))){
            //限价买入情况，做资产解冻余额计算
            if(taker.getOrderDirection().equals(OrderDirection.BUY)){
                setSurplusFrozen(taker);
            }
            //添加订单薄
            orderBook.addOrder(taker);
            //更新盘口数据
            tradePlateAddOrder(taker,tradePlate,orderBook.getBaseCoinScale());
        }else{
            //最后一个结果设置订单完成
            MatchResult matchResult = trades.get(trades.size() - 1);
            matchResult.getTakerResult().setFinished(true);
        }
        return trades;
    }

    /**
     * 限价单进盘口
     * @param order
     * @param tradePlate
     * @param baseCoinScale
     */
    private void tradePlateAddOrder(OrderLimit order,TradePlate tradePlate,int baseCoinScale) {
        //将订单添加到盘口里面
        tradePlate.add(order,baseCoinScale);
    }

    /**
     * 限价单买入情况下，需要计算剩余金额
     * @param taker
     */
    private void setSurplusFrozen(OrderLimit taker){
        //1.计算已经消耗的金额;
        taker.setSurplusFrozen(taker.getSurplusFrozen().subtract(taker.getTurnover()));
    }

    /**
     * 进行委托单的匹配撮合交易
     * @param taker 吃单（主动）
     * @param maker 挂单
     * @return 交易记录
     */
    private MatchResult processMath(OrderLimit taker, OrderLimit maker, OrderBook orderBook,TradePlate tradePlate) {
        //取maker挂单价格作为成交价，保护taker
        BigDecimal dealPrice = maker.getPrice();
        //这是taker需要的数量
        BigDecimal needAmount = calcTradeAmountTaker(taker);
        //这是maker能够提供的数量
        BigDecimal providerAmount = calcTradeAmountMaker(maker);
        //成交的数量
        BigDecimal dealAmount = needAmount.compareTo(providerAmount) >= 0 ? providerAmount : needAmount;

        // 计算成交额
        BigDecimal turnoverPrice = dealAmount.multiply(dealPrice);

        // 设置taker交易数据
        taker.setTradeAmount(taker.getTradeAmount().add(dealAmount));
        taker.setUnFinishAmount(taker.getUnFinishAmount().subtract(dealAmount));
        taker.setTurnover(taker.getTurnover().add(turnoverPrice));

        // 设计maker交易数据
        maker.setTradeAmount(maker.getTradeAmount().add(dealAmount));
        maker.setUnFinishAmount(maker.getUnFinishAmount().subtract(dealAmount));
        maker.setTurnover(taker.getTurnover().add(turnoverPrice));
        // maker只有是限价单买入的情况下，才对剩余金额做处理
        if(maker.getOrderDirection().equals(OrderDirection.BUY)){
            maker.setSurplusFrozen(maker.getSurplusFrozen().subtract(turnoverPrice));
        }
        //处理盘口：将挂单的数据做了一部分或全部消耗
        updateTradePlateAmount(maker, dealAmount,tradePlate,orderBook.getBaseCoinScale());

        //填充交易记录
        return generateMatchExchangeTrade(taker, maker,dealPrice, dealAmount,orderBook.getBaseCoinScale(),orderBook,tradePlate);
    }

    /**
     * 生成交易记录
     *
     * @param taker
     * @param maker
     * @param dealPrice
     * @param dealAmount
     * @return
     */
    private MatchResult generateMatchExchangeTrade(OrderLimit taker, OrderLimit maker,  BigDecimal dealPrice, BigDecimal dealAmount,int baseCoinScale,OrderBook orderBook,TradePlate tradePlate) {
        //taker信息
        OrderResult takerResult = OrderResult.builder()
                .userId(taker.getUserId())
                .orderId(taker.getOrderId())
                .orderDirection(taker.getOrderDirection())
                .build();
        //maker信息
        OrderResult makerResult = OrderResult.builder()
                .userId(maker.getUserId())
                .orderId(maker.getOrderId())
                .orderDirection(maker.getOrderDirection())
                .build();
        //返回结果
        MatchResult matchResult = MatchResult.builder()
                .symbol(taker.getSymbol())
                .takerResult(takerResult)
                .makerResult(makerResult)
                .matchTime(System.currentTimeMillis())
                .matchPrice(dealPrice)
                .matchAmount(dealAmount)
                .orderType(OrderType.LIMIT)
                .build();
        //判断maker是否已经完成了
        Boolean completed = ScalaCheckUtil.isCompleted(baseCoinScale, maker.getAmount().subtract(maker.getTradeAmount()));
        if(completed){
            makerResult.setFinished(true);
            updateTradePlateAmount(maker, maker.getAmount().subtract(maker.getTradeAmount()),tradePlate,orderBook.getBaseCoinScale());
        }else{
            makerResult.setFinished(false);
        }
        return matchResult;
    }

    /**
     * 盘口数据处理
     * @param maker
     * @param dealAmount
     * @param tradePlate
     * @param baseCoinScale
     */
    private void updateTradePlateAmount(OrderLimit maker, BigDecimal dealAmount,TradePlate tradePlate,int baseCoinScale) {
        log.info("limitPriceMatchServiceImpl trade plate remove order-id={} ",maker.getOrderId());
        tradePlate.remove(maker, dealAmount,baseCoinScale);
    }

    /**
     * 限价单买/卖数量是否低于最低精度
     * @param orderBook
     * @param taker
     * @return
     */
    private Boolean orderAmountIllegal(OrderBook orderBook, OrderLimit taker) {
        BigDecimal min = BigDecimal.ONE.movePointLeft(orderBook.getBaseCoinScale());
        return taker.getAmount().compareTo(min) < 0;
    }

    /**
     * 根据方向判断是否能够成交
     * @param taker
     * @param makerPrice
     * @return
     */
    private Boolean priceNotMatch(OrderLimit taker, BigDecimal makerPrice) {
        // 我花10块钱买东西，别人的东西价格大于10块钱，我就买不了
        if (taker.getOrderDirection().equals(OrderDirection.BUY)
                && taker.getPrice().compareTo(makerPrice) < 0) {
            return true;
        }
        //我出售一个东西，如果别人价格只出5块钱，则不会卖
        if (taker.getOrderDirection().equals(OrderDirection.SELL)
                && taker.getPrice().compareTo(makerPrice) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取taker的未成交了数量
     * @param taker
     * @return
     */
    private BigDecimal calcTradeAmountTaker(OrderLimit taker) {
        return taker.getUnFinishAmount();
    }

    /**
     * 获取maker的未成交数据量
     * @param maker
     * @return
     */
    private BigDecimal calcTradeAmountMaker(OrderLimit maker) {
        return maker.getAmount().subtract(maker.getTradeAmount());
    }

    /**
     * 撤单的返回结果
     * @param taker
     * @return
     */
    private MatchResult generateCancelOrderExchangeTrade(OrderLimit taker) {
        OrderResult orderResult = OrderResult.builder()
                .orderId(taker.getOrderId())
                .userId(taker.getUserId())
                .build();
        MatchResult result = MatchResult.builder()
                .symbol(taker.getSymbol())
                .orderType(OrderType.LIMIT)
                .takerResult(orderResult)
                .build();
        if (taker.getTradeAmount()==null||taker.getTradeAmount().compareTo(BigDecimal.ZERO)==0){
            result.setCompleteStatus(CompleteStatus.CANCEL_ALL);
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() {
        MatchServiceFactory.register(OrderType.LIMIT, this);
    }
}
