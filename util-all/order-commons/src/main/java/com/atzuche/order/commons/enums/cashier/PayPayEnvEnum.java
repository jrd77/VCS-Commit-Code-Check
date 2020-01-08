package com.atzuche.order.commons.enums.cashier;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum PayPayEnvEnum {
    PRO("PRO","10"),
    PRE("PRE","20"),
    DEV("DEV","30"),
    TEST1("TEST1","11"),
    TEST2("TEST2","12"),
    TEST3("TEST3","13"),
    TEST4("TEST4","14"),
    TEST5("TEST5","15"),
    TEST6("TEST6","16"),
    TEST7("TEST7","17"),
    TEST9("TEST9","19"),



    ;

    private String code;
    private String text;

    private PayPayEnvEnum(String code, String text) {
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
        for(PayPayEnvEnum payTypeEnum : PayPayEnvEnum.values()){
            if(payTypeEnum.code.equals(code)){
                return payTypeEnum.text;
            }
        }
        return null;
    }
}
