package com.atzuche.order.commons.enums;

/**
 * @author 胡春林
 * 配送信息错误编码
 */
public enum  DeliveryErrorCode{

    SEND_REN_YUN_HTTP_ERROR("300001","请求仁云接口出错"),
    DELIVERY_PARAMS_ERROR("300002","接受参数出错"),
    DELIVERY_MOUDLE_ERROR("300003","配送模块业务出错"),
    NO_DELIVERY_INFO("300004","该订单没有相关的配送信息"),
    PIC_KEY_PARAM_ERROR("300072","图片相对路径+图片名称不能为空"),
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
