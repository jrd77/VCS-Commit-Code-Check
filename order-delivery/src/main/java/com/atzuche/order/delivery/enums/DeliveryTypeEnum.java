package com.atzuche.order.delivery.enums;

/**
 * @author 胡春林
 * 配送服务接口业务类型
 */
public enum DeliveryTypeEnum {

    ADD_TYPE(1, "新增订单"),
    UPDATE_TYPE(2, "更新订单"),
    CANCEL_TYPE(3, "取消订单");

    private Integer value;

    private String name;

    DeliveryTypeEnum(Integer value, String name) {
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
