package com.atzuche.order.commons.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 取消订单使用角色
 *
 * @author pengcheng.fu
 * @date 2020/1/7 16:29
 */
@Getter
public enum CancelSourceEnum {
    /**
     * 租客
     **/
    RENTER(2, "租客"),
    /**
     * 车主
     **/
    OWNER(1, "车主"),

    /**
     * 平台
     **/
    PLATFORM(3, "平台"),

    /**
     * 平台代租客取消
     **/
    INSTEAD_OF_RENTER(4, "代租客取消"),


    /**
     * 平台代车主取消
     **/
    INSTEAD_OF_OWNER(5, "代车主取消")

    ;





    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;

    CancelSourceEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static CancelSourceEnum from(Integer code) {
        if(null == code) {
            return null;
        }
        CancelSourceEnum[] sourceEnums = values();
        for (CancelSourceEnum source : sourceEnums) {
            if (source.code.intValue() == code.intValue()) {
                return source;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }
}
