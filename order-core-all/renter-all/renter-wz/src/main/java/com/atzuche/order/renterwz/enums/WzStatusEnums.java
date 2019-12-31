package com.atzuche.order.renterwz.enums;

/**
 * WzStatusEnums
 *
 * @author shisong
 * @date 2019/12/30
 */
public enum WzStatusEnums {
    STATUS_5(5,"未处理"),
    STATUS_10(10,"待租客处理"),
    STATUS_20(20,"已处理-无违章"),
    STATUS_25(25,"处理中-租客处理"),
    STATUS_26(26,"处理中-车主处理"),
    STATUS_35(35,"已处理-异常订单"),
    STATUS_40(40,"处理中-平台处理"),
    STATUS_45(45,"已处理-无信息"),
    STATUS_46(46,"处理中-无数据"),
    STATUS_50(50,"已处理-租客处理"),
    STATUS_51(51,"已处理-车主处理")
    ;

    private Integer status;

    private String statusDesc;

    WzStatusEnums(Integer status, String statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public static String getStatusDesc(Integer status){
        if(status == null){
            return "";
        }
        WzStatusEnums[] values = WzStatusEnums.values();
        for (WzStatusEnums value : values) {
            if(status.equals(value.getStatus())){
                return value.getStatusDesc();
            }
        }
        return "";
    }
}
