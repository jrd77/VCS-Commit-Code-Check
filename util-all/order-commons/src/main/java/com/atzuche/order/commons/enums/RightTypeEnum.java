package com.atzuche.order.commons.enums;

import lombok.Getter;

@Getter
public enum RightTypeEnum {
    //1-内部员工类别，2-会员标志类别，3-太保会员类别，4-任务类别
    STAFF(1,"内部员工类别"),
    MEMBER_FLAG(2,"会员标志类别"),
    CPIC(3,"太保会员类别"),
    TASK(4,"任务类别")
    ;
    private Integer code;
    private String name;

    RightTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
