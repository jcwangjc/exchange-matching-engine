package com.exchange.match.engine.algorithm.enums;

import lombok.Getter;

/**
 * @author sean
 */
@Getter
public enum OrderType {
    /**
     * 限价单
     */
    LIMIT(1, "limit"),
    /**
     * 市价单
     */
    MARKET(2, "market"),
    /**
     * 撤销单
     */
    CANCEL(3, "cancel"),
    ;

    public final Integer code;
    public final String desc;

    OrderType(Integer code, String label) {
        this.code = code;
        this.desc = label;
    }

    public static OrderType getOrderType(Integer code) {
        OrderType[] values = OrderType.values();
        for (OrderType orderType : values) {
            if (orderType.getCode().equals(code)) {
                return orderType;
            }
        }
        return null;
    }
}