package com.atzuche.order.transport.common;

/**
 * @author 胡春林
 * 超运能服务错误码
 */
public enum TransPortErrorCode {

    TRANS_PORT_ERROR("300010", "超运能出错"),
    ;

    private String code;
    private String text;

    TransPortErrorCode(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getValue() {
        return code;
    }

    public String getName() {
        return text;
    }
}

