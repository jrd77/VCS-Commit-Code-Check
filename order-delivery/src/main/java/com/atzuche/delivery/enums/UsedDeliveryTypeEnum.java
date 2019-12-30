package com.atzuche.delivery.enums;

/**
 * @author 胡春林
 * 是否使用仁云配送
 */
public enum  UsedDeliveryTypeEnum {

    NO_USED(0, "不使用"),
    USED(1, "使用");

    private Integer value;

    private String name;

    UsedDeliveryTypeEnum(Integer value, String name) {
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
