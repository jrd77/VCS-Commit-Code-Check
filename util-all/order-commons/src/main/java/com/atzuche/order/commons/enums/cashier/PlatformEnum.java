package com.atzuche.order.commons.enums.cashier;

/**
 * 操作平台
 */
public enum PlatformEnum {

    /**
     *  C端0,后台1
      */
    C_TERMINAL(Byte.parseByte("0"),"前端"),
    OPERATION_TERMINAL(Byte.parseByte("1"),"后台")

    ;

    private Byte code;
    private String text;


    private PlatformEnum(Byte code, String text) {
        this.code = code;
        this.text = text;
    }

    public Byte getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
