package com.exchange.match.engine.algorithm.enums;

import lombok.Getter;

/**
 * @author : laoA
 * @describe : 交易方向枚举表达
 * @email : laoa@markcoin.net
 */
@Getter
public enum OrderDirection {
    BUY(1, "买入"),
    SELL(2, "卖出");
    private Integer code;
    private String desc;

    OrderDirection(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
