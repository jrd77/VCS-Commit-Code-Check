package com.atzuche.order.commons.enums.detain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 风控暂扣租车押金原因
 *
 * @author pengcheng.fu
 * @date 2020/3/2411:30
 */
@Getter
public enum DetainRiskReasonEnum {

    /**
     * 逾期不还
     **/
    ONE("1", "逾期不还"),
    /**
     * 赛道测试
     **/
    TWO("2", "赛道测试"),
    /**
     * 涉嫌风控事件
     **/
    THREE("3", "涉嫌风控事件"),
    /**
     * 警方扣车
     **/
    FOUR("4", "警方扣车"),

    /**
     * 其他
     **/
    FIVES("5", "其他");


    /**
     * code
     */
    private String code;
    /**
     * name
     */
    private String name;


    DetainRiskReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static DetainRiskReasonEnum from(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        DetainRiskReasonEnum[] enums = values();
        for (DetainRiskReasonEnum reason : enums) {
            if (StringUtils.equals(reason.getCode(), code)) {
                return reason;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }

}
