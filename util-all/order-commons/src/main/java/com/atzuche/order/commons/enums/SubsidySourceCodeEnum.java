package com.atzuche.order.commons.enums;

import lombok.Getter;
/*
 * @Author ZhangBin
 * @Date 2019/12/25 20:31
 * @Description: 补贴方  补贴来源方 编码
 *
 **/
@Getter
public enum SubsidySourceCodeEnum {
    RENTER("1","租客"),
    OWNER("2","车主"),
    PLATFORM("3","平台")
            ;

    private String code;
    private String desc;

    SubsidySourceCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
