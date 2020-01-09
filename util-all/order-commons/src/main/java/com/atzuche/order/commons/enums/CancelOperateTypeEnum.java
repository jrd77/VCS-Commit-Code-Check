package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 取消/拒单操作类型
 *
 * @author pengcheng.fu
 * @date 2020/1/9 14:48
 */
@Getter
public enum CancelOperateTypeEnum {

    /**
     * 操作类型-取消
     **/
    CANCEL_ORDER("1", "取消"),
    /**
     * 操作类型-拒单
     **/
    REFUSE_ORDER("2", "拒单");


    private String code;

    private String name;

    CancelOperateTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
