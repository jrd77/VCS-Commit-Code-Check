package com.atzuche.order.transport.common;

/**
 * @author 胡春林
 * 费用服务错误码
 */
public enum TransPortErrorCode {

    TRANS_PORT_ERROR("300010", "超运能出错"),
    GET_RETURN_CAR_ERROR("300011", "租客取还车费用出错"),
    TRANS_PORT_PARAMS_ERROR("300012", "取还车相关费用参数出错"),
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

