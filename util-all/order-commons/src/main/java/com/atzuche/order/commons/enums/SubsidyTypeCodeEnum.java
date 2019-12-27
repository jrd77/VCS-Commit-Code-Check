package com.atzuche.order.commons.enums;

import lombok.Getter;

/*
 * @Author ZhangBin
 * @Date 2019/12/25 20:31
 * @Description: 补贴类型 编码
 *
 **/
@Getter
public enum SubsidyTypeCodeEnum {
    GET_CAR("1","取车"),
    RETURN_CAR("2","还车")

            ;

    private String code;
    private String desc;

    SubsidyTypeCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
