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
    VIRTUAL_PAY("91","虚拟支付"),
    UNIONPAY_POS("21","银联POS机"),
    BILL99_POS("22","快钱POS机"),
    TRANSFER("23","转账"),
    CASH("24","现金"),
    CHANNEL("29","渠道支付"),
    UNIONPAY_BACK("25","银联后台"),
    ALIPAY_OFFLINE("26","支付宝线下支付"),
    DEPOSIT("28","押金延续"),
    OFFLINEPAY("30","线下真实支付"),
    DEFAULT("99","默认")
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
        throw new RuntimeException("code:"+code+" is invalid");
    }

    public static PaySourceEnum from(String code){
        PaySourceEnum[] values = values();
        for(PaySourceEnum source:values){
            if(source.getCode().equals(code)){
                return source;
            }
        }
        throw new RuntimeException("code:"+code+ " is invalid");
    }
}
