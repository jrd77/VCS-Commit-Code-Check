package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 取消订单使用角色
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:29
 */
@Getter
public enum MemRoleEnum {
    /**
     * 租客
     **/
    RENTER("2", "租客"),
    /**
     * 车主
     **/
    OWNER("1", "车主"),

    /**
     * 平台
     **/
    PLATFORM("3", "平台"),



    ;





    /**
     * code
     */
    private String code;
    /**
     * msg
     */
    private String msg;

    MemRoleEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
