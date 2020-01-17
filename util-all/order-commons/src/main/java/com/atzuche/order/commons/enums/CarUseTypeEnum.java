package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum CarUseTypeEnum {
    MPV(1,"MPV"),
    SUV(2,"SUV"),
    ZXC(3,"中型车"),
    ZDXC(4,"中大型车"),
    OTHER(5,"其它"),
    KC(6,"客车"),
    XXC(7,"小型车"),
    WXC(8,"微型车"),
    FC(9,"房车"),
    PK(10,"皮卡"),
    JCXC(11,"紧凑型车"),
    HHC(12,"豪华车"),
    PC(13,"跑车"),
    MBC(14,"面包车");

    private int code;

    private String name;

    CarUseTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code){
        if(code == null){
            return null;
        }
        CarUseTypeEnum[] values = CarUseTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code){
                return values[i].name;
            }
        }
        return null;
    }
}
