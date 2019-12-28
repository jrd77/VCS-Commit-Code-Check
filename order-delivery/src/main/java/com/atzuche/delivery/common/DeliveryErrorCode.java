package com.atzuche.delivery.common;

/**
 * @author 胡春林
 * 配送信息错误编码
 */
public enum  DeliveryErrorCode{

    SEND_REN_YUN_HTTP_ERROR("300001","请求仁云接口出错"),
    DELIVERY_PARAMS_ERROR("300002","配送接口接受参数出错"),
    ;

    private String code;
    private String text;

     DeliveryErrorCode(String code, String text) {
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
