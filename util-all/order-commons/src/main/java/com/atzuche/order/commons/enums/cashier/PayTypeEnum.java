package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum PayTypeEnum {


    PAY_PUR("01","消费"),
    PAY_PRE("02","预授权"),
    PUR_VOID("31","消费撤销"),
    PRE_VOID("32","预授权撤销"),
    PRE_FINISH("03","预授权完成"),
    PUR_RETURN("04","退货"),


    ;

    private String code;
    private String text;

    private PayTypeEnum(String code, String text) {
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
        for(PayTypeEnum payTypeEnum :PayTypeEnum.values()){
            if(payTypeEnum.code.equals(code)){
                return payTypeEnum.text;
            }
        }
        return null;
    }
}
