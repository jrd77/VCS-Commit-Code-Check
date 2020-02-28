package com.atzuche.order.mq.enums;

/**
 * @author 胡春林
 */
public enum PushMessageTypeEnum {

    /*##################################################################PUSH信息模版数据（新订单）###################################################################################################################*/
    RENTER_PAY_CAR_2_OWNER("10", "租客已经支付完成租车费用"),
    RENTER_NO_PAY_CAR("19", "由于您未在规定时间内支付租车费用，您的订单“$carBrandName$ $carTypeName$”已被取消"),
    RENTER_NO_PAY_CAR_2_OWNER("20", "因租客未能完成租车费用支付，您$plateNum$的订单已取消"),
    RENTER_NO_PAY_ILLEGAL("21", "您还未支付预定车辆的押金，请在$leftHours$小时内完成支付。否则该订单将被取消，并扣除您的违约费用"),
    RENTER_NO_PAY_ILLEGAL_CANCEL("23", "由于您未在规定时间内支付押金，订单已被取消，系统将按平台规则扣除您的违约金"),
    RENTER_NO_PAY_ILLEGAL_2_OWNER("24", "因租客未能完成押金支付，您$plateNum$的订单已取消"),
    RENTER_PAY_CAR_SUCCESS("287", "您已成功支付凹凸自营车辆【$plateNum$】的租车费用，如有特殊用车需要请告诉我们 http://1t.click/hej，我们将竭力为您提供更舒适便捷的服务"),
    ;

    private String value;

    private String name;

    PushMessageTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取push消息模版
     * @param value
     * @return
     */
    public static String getSmsTemplate(String value) {
        for (PushMessageTypeEnum pushMessageTypeEnum : values()) {
            if (pushMessageTypeEnum.getValue().equals(value)) {
                return pushMessageTypeEnum.getName();
            }
        }
        return null;
    }

}
