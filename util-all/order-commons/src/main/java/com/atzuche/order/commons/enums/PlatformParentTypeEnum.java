package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum PlatformParentTypeEnum {
    APP("1","APP"),
    XCX("2","小程序"),
    WX("3","微信"),
    ALIPAY("4","支付宝"),
    PC("5","PC页面"),
    H5("6","H5页面"),
    CONSOLE("7","管理后台"),
    API("8","API");

    private String code;

    private String name;

    PlatformParentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }

        PlatformParentTypeEnum[] values = PlatformParentTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
