package com.exchange.match.engine.algorithm.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *  @author : laoA
 *  @email : laoa@markcoin.net
 *  @describe : 撮合模型——通用模型
 */
public class OrderMerge<T> implements Serializable {
    protected TreeMap<Long, T> orders;

    public OrderMerge(){
        orders=new TreeMap<Long, T>((Comparator.naturalOrder()));
    }

    public void add(Long orderId,T order) {
        this.orders.put(orderId,order);
    }

    public T get(){
        return orders.get(0);
    }

    public int size(){
        return orders.size();
    }

    public Iterator<Long> iterator(){
        return orders.keySet().iterator();
    }

    public TreeMap<Long, T> getOrders() {
        return orders;
    }

    public void setOrders(TreeMap<Long, T> orders) {
        this.orders = orders;
    }
}
