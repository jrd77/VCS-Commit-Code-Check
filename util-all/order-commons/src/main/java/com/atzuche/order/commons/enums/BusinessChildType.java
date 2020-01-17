package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum BusinessChildType {
    OTA_XC("1","OTA-携程"),
    OTA_TC("2","OTA-同城"),
    OTA_FZ("3","OTA-飞猪"),
    OTA_ZZC("4","OTA-租租车"),
    DBC_CXDBC("5","代步车-出险代步车"),
    DBC_2DBC("6","代步车-2*2代步车"),
    DBC_QMXD("7","代步车-券码下单"),
    DBC_TGC("8","代步车-特供车");

    private String code;

    private String name;

    BusinessChildType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }
        BusinessChildType[] values = BusinessChildType.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code) ){
                return values[i].name;
            }
        }
        return null;
    }
}
