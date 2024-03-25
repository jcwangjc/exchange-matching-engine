package com.exchange.match.engine.algorithm.enums;

/**
 * @author : laoA
 * @describe : 订单的交易状态
 * @email : laoa@markcoin.net
 */
public enum CompleteStatus {
    CANCEL_ALL("全部撤单，没有任何成交");
    private String text;
    CompleteStatus(String text){
        this.text=text;
    }
}
