package com.exchange.match.engine.algorithm.model;

import com.exchange.match.engine.algorithm.enums.OrderDirection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *  @author : laoA
 *  @email : laoa@markcoin.net
 *  @describe : 撮合模型——委托单账本
 */
@Slf4j
@Data
public class OrderBook implements Serializable {
    /**
     * 买入的限价交易，价格从高到低
     * eg: 价格越高，越容易买到
     * key: 价格
     * MergeOrder: 同价格的订单，订单按时间排序
     */
    protected TreeMap<BigDecimal, OrderMerge> buyLimitPrice;
    /**
     * 卖出的限价交易，价格从低到高
     * eg：价格越低，卖出的越容易
     */
    protected TreeMap<BigDecimal, OrderMerge> sellLimitPrice;

    /**
     * 交易的币种
     */
    protected String symbol;
    /**
     * 交易币种的精度(usdt)
     */
    protected int coinScale;
    /**
     * 基币的精度(btc)
     */
    protected int baseCoinScale;
    /**
     * 日期格式器
     */
    protected SimpleDateFormat dateTimeFormat;
    /**
     * 默认构造器
     */
    protected OrderBook(String symbol){
        this(symbol,16,16);
    }
    /**
     * 自定义构造器
     * @param symbol
     * @param coinScale
     * @param baseCoinScale
     */
    public OrderBook(String symbol, int coinScale, int baseCoinScale){
        this.symbol=symbol;
        this.coinScale=coinScale;
        this.baseCoinScale=baseCoinScale;
        this.initialize();
    }

    /**
     * 初始化队列
     */
    public void initialize(){
        log.info(".......init CoinTrader for symbol {}",symbol);
        //加载买入比较器，价格从大到小
        buyLimitPrice = new TreeMap<>(Comparator.reverseOrder());
        //加载卖出比较器，价格从小到大
        sellLimitPrice = new TreeMap<>(Comparator.naturalOrder());
        //日期格式化工具
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取当前的交易队列
     */
    public TreeMap<BigDecimal, OrderMerge> getDepth(OrderDirection orderDirection){
        return orderDirection==OrderDirection.BUY ? buyLimitPrice:sellLimitPrice;
    }

    /**
     * 获取当前的交易队列的迭代器
     * @return
     */
    public Iterator<Map.Entry<BigDecimal, OrderMerge>> getDepthIterator(OrderDirection orderDirection){
        return getDepth(orderDirection).entrySet().iterator();
    }

    /**
     * 获取对手的交易队列的迭代器
     * @return
     */
    public Iterator<Map.Entry<BigDecimal, OrderMerge>> getOpponentDepthIterator(Integer direct){
        if (direct.equals(OrderDirection.BUY.getCode())) {
            return getDepthIterator(OrderDirection.SELL);
        } else {
            return getDepthIterator(OrderDirection.BUY);
        }
    }

    /**
     * 添加订单进入我们的队列里面
     * @return
     */
    public void addOrder(OrderLimit order){
        log.info("add order {}",order.getOrderId());
        //将订单加入委托账本
        TreeMap<BigDecimal, OrderMerge> depth = getDepth(order.getOrderDirection());
        OrderMerge<OrderLimit> mergeOrder = depth.get(order.getPrice());
        if(mergeOrder==null) {
            mergeOrder = new OrderMerge<OrderLimit>();
            depth.put(order.getPrice(),mergeOrder);
        }
        mergeOrder.add(order.getOrderId(),order);
    }

    /**
     * 从订单薄移除订单
     * @return
     */
    public Order removeOrder(OrderLimit order){
        log.info("remove order {}",order.getOrderId());
        TreeMap<BigDecimal, OrderMerge> depth = getDepth(order.getOrderDirection());
        OrderMerge mergeOrder = depth.get(order.getPrice());
        if(mergeOrder==null){
            log.error("orderId:{}不存在，移除订单失败", order.getOrderId());
            return null;
        }
        TreeMap<Long, Order> orders = mergeOrder.getOrders();
        Order oldOrder = orders.remove(order.getOrderId());
        //删除之后，看合并订单的大小,如果数量为0就删除队列里面的数据
        if(mergeOrder.size()==0){
            depth.remove(order.getPrice());
        }
        return oldOrder;
    }

    /**
     * 获取排在队列里面的第一个数据
     * @return
     */
    public Map.Entry<BigDecimal, OrderMerge> getBestSuiOrder(OrderDirection orderDirection){
        return getDepth(orderDirection).firstEntry();
    }
}
