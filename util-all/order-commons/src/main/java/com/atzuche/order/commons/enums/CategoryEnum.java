package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CategoryEnum {
    ORDINARY("1","普通"),
    SET_MEAL("2","套餐");

    private String code;

    private String name;

    CategoryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }
        CategoryEnum[] values = CategoryEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
