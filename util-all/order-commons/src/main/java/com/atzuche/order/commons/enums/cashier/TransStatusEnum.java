package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum TransStatusEnum {

//00成功，01进行中，03失败
    PAY_SUCCESS("00","成功"),
    PAY_ING("01","进行中"),
    PAY_FAIL("03","失败"),


    ;

    private String code;
    private String text;

    private TransStatusEnum(String code, String text) {
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
        for(TransStatusEnum payTypeEnum : TransStatusEnum.values()){
            if(payTypeEnum.code.equals(code)){
                return payTypeEnum.text;
            }
        }
        return null;
    }
}
