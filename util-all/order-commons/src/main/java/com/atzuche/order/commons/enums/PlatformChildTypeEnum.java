package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum PlatformChildTypeEnum {
    APP_IOS("1","APP-IOS"),
    APP_ANDROID("2","APP-ANDROID"),
    XCX_ALIPAY("3","小程序-支付宝"),
    XCX_WX("4","小程序-微信"),
    XCX_BAIDU("5","小程序-百度");

    private String code;

    private String name;

    PlatformChildTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public static String getNameByCode(String code){
        if(code == null){
            return null;
        }

        PlatformChildTypeEnum[] values = PlatformChildTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode().equals(code)){
                return values[i].name;
            }
        }
        return null;
    }
}
