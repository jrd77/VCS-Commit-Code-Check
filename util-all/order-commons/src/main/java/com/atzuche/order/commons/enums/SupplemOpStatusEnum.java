package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 补付记录操作状态
 *
 * @author pengcheng.fu
 * @date 2020/3/19 11:15
 */
@Getter
public enum SupplemOpStatusEnum {

    /**
     * 补付记录操作状态:0,待提交
     */
    OP_STATUS_NO_COMMIT(0, "待提交"),

    /**
     * 补付记录操作状态:1,已生效
     */
    OP_STATUS_TAKE_EFFECT(1, "已生效"),

    /**
     * 补付记录操作状态:2,已失效
     */
    OP_STATUS_LOSE_EFFECT(2, "已失效"),

    /**
     * 补付记录操作状态:3,已撤回
     */
    OP_STATUS_UNDO(3, "已撤回"),

    ;


    /**
     * 操作状态编码
     */
    int code;

    /**
     * 操作状态文案
     */
    String text;

    SupplemOpStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static SupplemOpStatusEnum from(int code) {
        SupplemOpStatusEnum[] opStatusEnums = values();
        for (SupplemOpStatusEnum opStatus : opStatusEnums) {
            if (opStatus.code == code) {
                return opStatus;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }
}
