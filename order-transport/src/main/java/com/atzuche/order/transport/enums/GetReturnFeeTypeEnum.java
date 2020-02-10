package com.atzuche.order.transport.enums;

/**
 * @author 胡春林
 * 配送费用类型
 */
public enum  GetReturnFeeTypeEnum {

    GET_RETURN_FEE_TYPE(1, "取送车费用"),
    CAR_OVER_COST_TYPE(2, "超运能费用"),
    CAR_OVER_KM_TYPE(3, "超历程费用");

    private Integer value;

    private String name;

    GetReturnFeeTypeEnum(Integer value, String name) {
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
