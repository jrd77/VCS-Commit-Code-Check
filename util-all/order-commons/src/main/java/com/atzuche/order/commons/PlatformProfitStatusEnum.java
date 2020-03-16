package com.atzuche.order.commons;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2020/3/4 14:55
 * @Description: 平台订单收益计算表-status
 *
 **/
@Getter
@AllArgsConstructor
public enum PlatformProfitStatusEnum {
    WZ_SETTLE(0,"违章结算"),
    ORDER_SETTLE(1,"订单结算"),
    CANCEL_SETTLE(2,"取消订单");

    private int code;

    private String desc;

}
