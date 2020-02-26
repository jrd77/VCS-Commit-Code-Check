package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum PaySourceEnum {


    WALLET_PAY("00","钱包支付"),
    UNIONPAY("01","银联"),
    AT_OFFLINE("02","线下支付"),
    ALIPAY("06","支付宝普通预授权"),
    WEIXIN_APP("07","微信App"),
    BILL99("08","快钱支付(统一app和H5)"),
    APPLEPAY("12","Applepay"),
    WEIXIN_MP("13","微信公众号"),
    LIANLIANPAY("14","连连支付(统一app和H5)"),
    WEIXIN_H5("15","微信H5"),
    ALIPAY_CREDIT("16","支付宝信用预授权免押，芝麻信用"),
    DEFAULT("99","默认"),

    ;

    private String code;
    private String text;

    private PaySourceEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 根据code取text
     * @param code
     * @return
     */
    public static String getFlagText(String code){
        if(Objects.isNull(code)){
            return null;
        }
        for(PaySourceEnum paySourceEnum :PaySourceEnum.values()){
            if(paySourceEnum.code.equals(code)){
                return paySourceEnum.text;
            }
        }
        return null;
    }
}
