package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum PaySourceEnum {
    ON_LINE("1","线上"),
    UNDER_LINE("2","线下");

    private String code;
    private String name;

    PaySourceEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code){
        if(code == null){
            return "线上"; //默认值。
        }

        PaySourceEnum[] values = PaySourceEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return "线上";
    }
}
