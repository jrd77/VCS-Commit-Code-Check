package com.atzuche.delivery.enums;

/**
 * @author 胡春林
 * 交接机类型
 */
public enum   ServiceTypeEnum {

    BACK_TYPE("back"),
    TAKE_TYPE("take");

    private String value;


    ServiceTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
