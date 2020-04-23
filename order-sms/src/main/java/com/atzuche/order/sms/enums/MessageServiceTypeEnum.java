package com.atzuche.order.sms.enums;

/**
 * @author 胡春林
 */
public enum MessageServiceTypeEnum {

    /*##################################################################信息服务模版数据（新订单）###################################################################################################################*/
    CANCEL_ORDER_SUCCESS_SERVICE("action.renter.order.cancel", "CancelOrderSuccessService"),
    CREATE_ORDER_SUCCESS_SERVICE("action.order.create", "CreateOrderSuccessService"),
    ORDER_PAY_RENT_COST_SUCCESS_SERVICE("action.renter.order.paySuccess", "OrderPayRentCostSuccessService"),
    OWNER_AGREE_ORDER_SUCCESS_SERVICE("action.owner.order.agree", "OwnerAgreeOrderSuccessService"),
    OWNER_RETURN_CAR_SUCCESS_SERVICE("action.owner.confirm.returnCar", "OwnerReturnCarSuccessService"),
    ;

    private String value;

    private String name;

    MessageServiceTypeEnum(String name, String value) {
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
     * 获取服务模版
     * @param name
     * @return
     */
    public static String getSmsServiceTemplate(String name) {
        for (MessageServiceTypeEnum messageServiceTypeEnum : values()) {
            if (messageServiceTypeEnum.getName().equals(name)) {
                return messageServiceTypeEnum.getValue();
            }
        }
        return null;
    }
}
