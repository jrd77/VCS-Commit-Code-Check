package com.atzuche.order.commons.enums.detain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 理赔暂扣租车押金原因
 *
 * @author pengcheng.fu
 * @date 2020/3/2411:30
 */
@Getter
public enum DetainClaimsReasonEnum {

    /**
     * 保险责任内无超险暂扣
     **/
    ONE("1", "保险责任内无超险暂扣"),

    /**
     * 保险责任外暂扣
     **/
    TWO("2", "保险责任外暂扣"),

    /**
     * 其他
     **/
    THREE("3", "其他");


    /**
     * code
     */
    private String code;
    /**
     * name
     */
    private String name;


    DetainClaimsReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static DetainClaimsReasonEnum from(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        DetainClaimsReasonEnum[] enums = values();
        for (DetainClaimsReasonEnum reason : enums) {
            if (StringUtils.equals(reason.getCode(), code)) {
                return reason;
            }
        }

        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }

}
