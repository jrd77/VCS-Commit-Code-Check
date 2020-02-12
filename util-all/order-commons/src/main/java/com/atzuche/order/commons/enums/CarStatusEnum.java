package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CarStatusEnum {
    HAVE_GPS(1,"车辆上架安装了GPS"),
    NO_GPS(7,"车辆上架未安装GPS"),
    CONSOLE_RENT(90,"后台可租的车");

    private int code;

    private String name;

    CarStatusEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code){
        if(code == null){
            return null;
        }
        CarStatusEnum[] values = CarStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i].name;
            }
        }
        return null;
    }
}
