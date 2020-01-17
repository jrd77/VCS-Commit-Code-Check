package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum GearboxTypeEnum {
    automatic(1,"自动"),
    Manual(2,"手动");

    private int code;

    private String name;

    GearboxTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(Integer code){
        if(code == null){
            return null;
        }
        GearboxTypeEnum[] values = GearboxTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i].name;
            }
        }
        return null;
    }
}
