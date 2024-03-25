package com.exchange.match.engine.algorithm.disruptor.event;

import java.io.Serializable;

/**
 *  describe : 事件
 *  author : laoA
 *  email : laoa@markcoin.net
 */
public class OrderEvent implements Serializable {

    /**
     * 事件携带的数据
     */
    protected transient Object source;

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public void clear(){
        source=null;
    }
}
