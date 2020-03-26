/**
 * 
 */
package com.atzuche.order.commons.enums.detain;

import lombok.Getter;

import java.util.Objects;

/**
 * @author hiabao.yan
 * 暂扣状态 处理状态 1：暂扣 2：取消暂扣
 */
@Getter
public enum DetainStatusEnum {
    /**
     * 未暂扣
     **/
    NO_DETAIN(0, "未暂扣"),
	/**
     * 暂扣
     **/
    DETAIN(1, "暂扣"),
    /**
     * 取消暂扣
     **/
    DETAIN_CANCEL(2, "取消暂扣"),

    ;

    /**
     * code
     */
    private int code;
    /**
     * msg
     */
    private String msg;

    DetainStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static DetainStatusEnum from(Integer code) {
        if (Objects.isNull(code)) {
            return null;
        }
        DetainStatusEnum[] enums = values();
        for (DetainStatusEnum status : enums) {
            if (status.code == code) {
                return status;
            }
        }

        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }
}
