package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum EntryCodeEnum {
    SC("1","收藏"),
    LSDD("2","历史订单"),
    JGG("3","九宫格"),
    ;

    private String code;

    private String name;

    EntryCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }
        EntryCodeEnum[] values = EntryCodeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
