package com.atzuche.order.coreapi.enums;

/**
 * @author 胡春林
 * 短信消息类型
 */
public enum ShortMessageCodeEnum {

    PRE_CAR_COST_4_HOURS(1, "预支付4小时内未完成订单"),
    PUSH_DEPOSETID_COST_4_HOURS(2, "4小时/2小時支付違章押金"),
            ;

    private Integer value;

    private String name;

    ShortMessageCodeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
