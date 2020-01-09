package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 车主同意类型
 *
 * @author pengcheng.fu
 * @date 2020/1/9 17:36
 */
@Getter
public enum OwnerAgreeTypeEnum {


    /**
     * 已支付
     **/
    UNPROCESSED(0, "未处理"),
    /**
     * 已支付
     **/
    ARGEE(2, "已同意"),
    /**
     * 待支付
     **/
    REFUSE(3, "已拒绝");


    private int code;
    private String name;

    /**
     * constructor
     *
     * @param code status value
     * @param name status description
     */
    OwnerAgreeTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * convert int value to OrderStatus
     *
     * @param code int value
     * @return OwnerAgreeTypeEnum
     */
    public OwnerAgreeTypeEnum from(int code) {
        OwnerAgreeTypeEnum[] statuses = values();
        for (OwnerAgreeTypeEnum s : statuses) {
            if (code == s.code) {
                return s;
            }
        }
        throw new RuntimeException("the value of status :" + code + " not supported,please check");
    }
}
