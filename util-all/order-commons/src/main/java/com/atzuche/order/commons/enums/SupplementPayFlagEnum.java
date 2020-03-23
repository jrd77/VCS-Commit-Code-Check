package com.atzuche.order.commons.enums;

import lombok.Getter;

/**
 * 补付记录支付状态
 *
 * @author pengcheng.fu
 * @date 2020/3/19 10:28
 */
@Getter
public enum SupplementPayFlagEnum {

    /**
     * 补付记录支付状态:0,无需支付
     */
    PAY_FLAG_NO_NEED_PAY(0, "无需支付"),

    /**
     * 补付记录支付状态:1,未支付
     */
    PAY_FLAG_NO_PAY(1, "未支付"),

    /**
     * 补付记录支付状态:2,已取消
     */
    PAY_FLAG_CANCEL(2, "已取消"),

    /**
     * 补付记录支付状态:3,已支付
     */
    PAY_FLAG_PAY_FINISH(3, "已支付"),

    /**
     * 补付记录支付状态:4,支付中
     */
    PAY_FLAG_PAY_ING(4, "支付中"),

    /**
     * 补付记录支付状态:5,支付失败
     */
    PAY_FLAG_PAY_FAIL(5, "支付失败"),

    /**
     * 补付记录支付状态:10,租车押金结算抵扣
     */
    PAY_FLAG_ZUCHE_DEPOSIT_SETTLE_DEDUCT(10, "租车押金结算抵扣"),

    /**
     * 补付记录支付状态:20,违章押金结算抵扣
     */
    PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_DEDUCT(20, "违章押金结算抵扣"),

    /**
     * 补付记录支付状态:30,违章押金结算转欠款
     */
    PAY_FLAG_VIOLATION_DEPOSIT_SETTLE_INTO_DEBT(30, "违章押金结算转欠款");


    /**
     * 支付状态编码
     */
    int code;

    /**
     * 支付状态文案
     */
    String text;


    SupplementPayFlagEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }


    public static SupplementPayFlagEnum from(int code) {
        SupplementPayFlagEnum[] payFlagEnums = values();
        for (SupplementPayFlagEnum payFlag : payFlagEnums) {
            if (payFlag.code == code) {
                return payFlag;
            }
        }
        throw new RuntimeException("the value of code :" + code + " not supported,please check");
    }

}
