package com.atzuche.order.commons.enums;

import lombok.Getter;
/*
 * @Author ZhangBin
 * @Date 2019/12/18 15:37
 * @Description: 是否内部员工枚举类
 * 
 **/
@Getter
public enum InternalStaffEnum {
    is_Staff(1,"internalStaff","是否内部员工","是否内部员工-是内部员工"),
    NOT_Staff(0,"internalStaff","是否内部员工","是否内部员工-不是内部员工")
    ;

    InternalStaffEnum(int index, String rightCode, String rightName, String rightDesc) {
        this.index = index;
        this.rightCode = rightCode;
        this.rightName = rightName;
        this.rightDesc = rightDesc;
    }

    /**
     * 序号
     */
    private int index;
    /**
     * 权益编码
     */
    private String rightCode;
    /**
     * 权益名称
     */
    private String rightName;
    /**
     * 权益描述
     */
    private String rightDesc;
}
