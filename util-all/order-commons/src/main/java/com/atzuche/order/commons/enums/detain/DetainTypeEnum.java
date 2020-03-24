package com.atzuche.order.commons.enums.detain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 暂扣类型枚举
 *
 * @author pengcheng.fu
 * @date 2020/3/24 11:25
 */

@Getter
public enum DetainTypeEnum {

    /**
     * 风控
     **/
    risk("1", "风控"),
    /**
     * 交易
     **/
    trans("2", "交易"),
    /**
     * 理赔
     **/
    claims("3", "理赔");


    /**
     * code
     */
    private String code;
    /**
     * name
     */
    private String name;


    DetainTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static DetainTypeEnum from(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        DetainTypeEnum[] typeEnums = values();
        for (DetainTypeEnum type : typeEnums) {
            if (StringUtils.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }
}
