package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CancelReasonEnum {
    RENTER_CANCEL("1","租客取消订单"),
    PLATFORM_REPLACE_RENTER_CANCEL("2","平台代租客取消订单"),
    PLATFORM_REPLACE_OWNER_CANCEL("3","平台代车主取消订单"),
    RENTER_AUDIT_NO_PASS("4","租客审核不通过（风控）"),
    VEHICLE("5","车载设备有问题"),
    CAR_LOWER("6","车辆已下架"),
    OWNER_CANCEL("7","车主取消订单"),
    RENTER_CANCEL_NO_FINE("8","租客取消不产生违约金（代步车）"),
    TEST("9","测试订单（交易）"),
    MECHANICAL_FAILURE("10","机械故障结束订单（交易）"),
    DISPATCH_SUC_NEW_ORDER("11","调度成功重新成单（交易）"),
    OWNER_REFUSE("12","车主拒绝")
    ;

    private String code;
    private String name;

    CancelReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
