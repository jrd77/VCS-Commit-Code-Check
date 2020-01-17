package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum ChoiceCarEnum {
    YES(1,"是"),
    NO(0,"不是");

    private int code;

    private String name;

    ChoiceCarEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer status){
        if(status == null){
            return null;
        }

        ChoiceCarEnum[] values = ChoiceCarEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == (status)){
                return values[i].name;
            }
        }
        return null;
    }
}
