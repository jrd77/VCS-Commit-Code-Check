package com.atzuche.order.commons.enums.detain;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 交易暂扣租车押金原因
 *
 * @author pengcheng.fu
 * @date 2020/3/2411:30
 */
@Getter
public enum DetainTransReasonEnum {

    /**
     * 超速暂扣
     **/
    ONE("1", "超速暂扣"),

    /**
     * 违章累计6分以上暂扣（系统自动）
     **/
    TWO("2", "违章累计6分以上暂扣（系统自动）"),

    /**
     * 续租转移押金
     **/
    THREE("3", "续租转移押金"),

    /**
     * 出险代步车转移押金
     **/
    FOUR("4", "出险代步车转移押金"),

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


    DetainTransReasonEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public static DetainTransReasonEnum from(String code) {
        if (StringUtils.isBlank(code)) {
            return FIVES;
        }
        DetainTransReasonEnum[] enums = values();
        for (DetainTransReasonEnum reason : enums) {
            if (StringUtils.equals(reason.getCode(), code)) {
                return reason;
            }
        }

        return FIVES;
    }

}
