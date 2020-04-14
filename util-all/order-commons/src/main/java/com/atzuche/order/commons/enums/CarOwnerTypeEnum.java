package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CarOwnerTypeEnum {
    A(5,"个人车主"),
    B(10,"租赁公司"),
    C(15,"其他" ),
    D(20,"托管车辆-交易用"),
    E(25,"托管车辆-工作用"),
    F(30,"短期托管车"),
    G(35,"代管车辆"),
    H(150,"代管车专供代步车");
    private int code;
    private String name;

    CarOwnerTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code){
        if(code == null ){
            return null;
        }
        CarOwnerTypeEnum[] values = CarOwnerTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i].getName();
            }
        }
        return null;
    }

    /**
     *是否是凹凸自營
     */
    public static Boolean isAuToByCode(Integer code){
        boolean result = false;
        if (code == null) {
            return result;
        }
        if (code == F.code || code == G.code) {
            result = true;
            return result;
        }
        return result;
    }
}
