package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 *   类型（系统退款0/手工退款1
 类型（系统0   手工1
 **/
@Getter
public enum SysOrHandEnum {
    /**
     * 手工
     **/
    HAND(1,"手工"),
    /**
     * 系统
     **/
    SYSTEM(0,"系统");


    private int status;
    private String desc;

    /**
     *  constructor
     * @param status status value
     * @param desc  status description
     */
    SysOrHandEnum(int status, String desc){
        this.status = status;
        this.desc = desc;
    }

    /**
     * convert int value to OrderStatus
     * @param status int value
     * @return
     */
    public SysOrHandEnum from(int status){
        SysOrHandEnum[] statuses = values();
        for(SysOrHandEnum s:statuses){
            if(status==s.status){
                return s;
            }
        }
        throw new RuntimeException("the value of status :"+status+" not supported,please check");
    }


}
