package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum BusinessParentTypeEnum {
    ota("1","OTA"),
    dbc("2","代步车"),
    lpk("3","礼品卡"),
    al("4","安联"),
    zy("5","自有");

    private String code;

    private String name;

    BusinessParentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }
        BusinessParentTypeEnum[] values = BusinessParentTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
