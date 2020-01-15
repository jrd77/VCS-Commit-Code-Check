package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum EffectiveEnum {
    IS_EFFECTIVE(1,"有效"),
    NOT_EFFECTIVE(0,"无效");

    private int code;
    private String name;

     EffectiveEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(Integer code){
         if(code == null){
            return null;
         }
        EffectiveEnum[] values = EffectiveEnum.values();
        for(int i=0;i<values.length;i++){
            if(code == values[i].getCode()){
                return values[i].getName();
            }
        }
        return null;
    }
}
